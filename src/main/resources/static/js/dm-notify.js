// ============================================
// DM 실시간 알림 (SSE + 인앱 팝업 + 소리 + 진동)
// ============================================
(function() {
  if (window.__dmNotifyInited) return;
  window.__dmNotifyInited = true;

  const token = localStorage.getItem('JWT_TOKEN');
  if (!token) return;

  // ── 스타일 주입 ───────────────────────────────────────
  const style = document.createElement('style');
  style.textContent = `
    .dm-notify-stack {
      position: fixed;
      top: 90px; right: 16px;
      z-index: 9999;
      display: flex; flex-direction: column; gap: 10px;
      pointer-events: none;
      max-width: calc(100vw - 32px);
    }
    .dm-notify-card {
      pointer-events: auto;
      background: #fff;
      border: 1px solid #D7E6F2;
      border-radius: 16px;
      box-shadow: 0 12px 32px -8px rgba(11,32,54,.25), 0 2px 8px rgba(11,32,54,.08);
      padding: 14px 16px;
      display: flex; gap: 12px;
      width: 340px;
      cursor: pointer;
      animation: dmIn .35s cubic-bezier(.2,.7,.2,1);
      transition: transform .15s, box-shadow .15s;
      position: relative;
      overflow: hidden;
    }
    .dm-notify-card::before {
      content:'';
      position: absolute; left: 0; top: 0; bottom: 0;
      width: 4px;
      background: linear-gradient(180deg, #1FA6D6, #B8A6FF);
    }
    .dm-notify-card:hover {
      transform: translateY(-2px);
      box-shadow: 0 16px 36px -8px rgba(31,166,214,.35), 0 2px 8px rgba(11,32,54,.1);
    }
    .dm-notify-card.closing { animation: dmOut .3s cubic-bezier(.2,.7,.2,1) forwards; }
    @keyframes dmIn {
      from { opacity: 0; transform: translateX(20px) scale(.95); }
      to { opacity: 1; transform: translateX(0) scale(1); }
    }
    @keyframes dmOut {
      from { opacity: 1; transform: translateX(0); }
      to { opacity: 0; transform: translateX(20px); }
    }
    .dm-notify-avatar {
      width: 42px; height: 42px; border-radius: 50%;
      background: linear-gradient(135deg, #1FA6D6, #7DD4C0);
      display: flex; align-items: center; justify-content: center;
      color: #fff; font-weight: 700; font-size: 16px;
      flex-shrink: 0;
    }
    .dm-notify-body { flex: 1; min-width: 0; }
    .dm-notify-header {
      display: flex; align-items: center; justify-content: space-between;
      margin-bottom: 4px;
    }
    .dm-notify-title {
      font-size: 14px; font-weight: 800; color: #0B2036;
      display: flex; align-items: center; gap: 6px;
      letter-spacing: -0.01em;
    }
    .dm-notify-title::before {
      content:''; width: 8px; height: 8px; border-radius: 50%;
      background: #2CCB86;
      box-shadow: 0 0 0 3px rgba(44,203,134,.28);
      animation: pulseDot 1.6s ease-in-out infinite;
    }
    @keyframes pulseDot {
      0%,100% { box-shadow: 0 0 0 3px rgba(44,203,134,.28); }
      50% { box-shadow: 0 0 0 6px rgba(44,203,134,.1); }
    }
    .dm-notify-close {
      width: 22px; height: 22px;
      border: none; background: none;
      cursor: pointer; padding: 0;
      color: #A6BED4; font-size: 18px;
      border-radius: 50%;
      display: flex; align-items: center; justify-content: center;
      transition: background .12s, color .12s;
    }
    .dm-notify-close:hover { background: #EBF4FB; color: #1FA6D6; }
    .dm-notify-content {
      font-size: 13px; color: #264766;
      line-height: 1.4;
      display: -webkit-box;
      -webkit-line-clamp: 2; -webkit-box-orient: vertical;
      overflow: hidden;
    }
    .dm-notify-content strong { color: #1FA6D6; }
    @media (max-width: 480px) {
      .dm-notify-stack { top: 80px; right: 10px; left: 10px; }
      .dm-notify-card { width: auto; }
    }
  `;
  document.head.appendChild(style);

  // ── 알림 컨테이너 ─────────────────────────────────────
  const stack = document.createElement('div');
  stack.className = 'dm-notify-stack';
  stack.id = 'dmNotifyStack';
  document.body.appendChild(stack);

  // ── 알림 사운드 (Web Audio API 짧은 비프) ───────────
  let audioCtx = null;
  function playSound() {
    try {
      if (!audioCtx) audioCtx = new (window.AudioContext || window.webkitAudioContext)();
      // 짧고 부드러운 종소리 (도-미)
      [880, 1175].forEach((freq, i) => {
        const osc = audioCtx.createOscillator();
        const gain = audioCtx.createGain();
        osc.frequency.value = freq;
        osc.type = 'sine';
        gain.gain.setValueAtTime(0, audioCtx.currentTime + i * 0.08);
        gain.gain.linearRampToValueAtTime(0.18, audioCtx.currentTime + i * 0.08 + 0.02);
        gain.gain.exponentialRampToValueAtTime(0.001, audioCtx.currentTime + i * 0.08 + 0.4);
        osc.connect(gain); gain.connect(audioCtx.destination);
        osc.start(audioCtx.currentTime + i * 0.08);
        osc.stop(audioCtx.currentTime + i * 0.08 + 0.4);
      });
    } catch(e) {}
  }

  function escapeHtml(s) {
    return String(s||'').replace(/&/g,'&amp;').replace(/</g,'&lt;')
      .replace(/>/g,'&gt;').replace(/"/g,'&quot;');
  }

  // ── 알림 카드 표시 ───────────────────────────────────
  function showNotifyCard(senderNickname, content, convIdx) {
    const card = document.createElement('div');
    card.className = 'dm-notify-card';
    const initial = (senderNickname || '?').charAt(0);
    card.innerHTML = `
      <div class="dm-notify-avatar">${escapeHtml(initial)}</div>
      <div class="dm-notify-body">
        <div class="dm-notify-header">
          <div class="dm-notify-title">새 메시지</div>
          <button class="dm-notify-close" onclick="event.stopPropagation();this.closest('.dm-notify-card').classList.add('closing');setTimeout(()=>this.closest('.dm-notify-card')?.remove(),300)">×</button>
        </div>
        <div class="dm-notify-content"><strong>${escapeHtml(senderNickname)}</strong> · ${escapeHtml(content)}</div>
      </div>`;
    card.onclick = () => { location.href = '/view/messages?conv=' + convIdx; };
    stack.appendChild(card);
    setTimeout(() => {
      if (card.parentNode) {
        card.classList.add('closing');
        setTimeout(() => card.remove(), 300);
      }
    }, 6000);
  }

  // ── SSE 연결 ─────────────────────────────────────────
  function connect() {
    if ('Notification' in window && Notification.permission === 'default') {
      Notification.requestPermission();
    }
    const es = new EventSource('/api/notifications/subscribe');
    es.addEventListener('dm', e => {
      try {
        const data = JSON.parse(e.data);
        // 현재 열린 대화방이면 무시
        if (window.activeConvIdx && data.convIdx === window.activeConvIdx) return;

        // 진동
        if (navigator.vibrate) navigator.vibrate([200, 100, 200]);
        // 소리
        playSound();
        // 인앱 알림 카드
        showNotifyCard(data.senderNickname, data.content, data.convIdx);
        // 브라우저 알림 (백그라운드용)
        if ('Notification' in window && Notification.permission === 'granted' && document.hidden) {
          const n = new Notification('💬 ' + data.senderNickname, {
            body: data.content, icon: '/favicon.ico', tag: 'dm-' + data.convIdx
          });
          n.onclick = () => { window.focus(); location.href = '/view/messages?conv=' + data.convIdx; };
          setTimeout(() => n.close(), 5000);
        }
        // 네비 배지
        const badge = document.getElementById('navUnreadBadge');
        if (badge) badge.style.display = 'block';
        // 메신저 페이지면 대화 목록 갱신
        if (typeof loadConversations === 'function') loadConversations();
      } catch(err) {}
    });
    es.onerror = () => { es.close(); setTimeout(connect, 8000); };
  }
  connect();
})();
