function showToast(type, message) {
  let container = document.getElementById('toast-container');

  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'me-toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `me-toast me-toast--${type}`;

  let title = 'Mensagem';
  let icon = 'ℹ';

  if (type === 'success') { title = 'Sucesso'; icon = '✔'; }
  if (type === 'error') { title = 'Erro'; icon = '✖'; }
  if (type === 'warning') { title = 'Atenção'; icon = '⚠'; }

  toast.innerHTML = `
    <div class="me-toast-icon">${icon}</div>
    <div class="me-toast-content">
      <div class="me-toast-title">${title}</div>
      <div class="me-toast-message">${message}</div>
    </div>
    <button class="me-toast-close">&times;</button>
  `;

  toast.querySelector('.me-toast-close').addEventListener('click', () => {
    closeToast(toast);
  });

  container.appendChild(toast);

  setTimeout(() => closeToast(toast), 4000);
}

function closeToast(toast) {
  toast.classList.add('hide');
  toast.addEventListener('animationend', () => toast.remove());
}


// =========================================
// LOGIN
// =========================================
async function login(){
  const role  = document.getElementById('role').value;
  const email = document.getElementById('email').value.trim();
  const senha = document.getElementById('senha').value.trim();

  if(!email || !senha){
    showToast('warning', 'Preencha e-mail e senha.');
    return;
  }

  const btn = document.getElementById('btnLogin');
  const originalText = btn.textContent;
  btn.classList.add('is-loading');
  btn.textContent = 'Entrando...';

  try{
    const resp = await fetch(`${API_BASE_URL}/auth/login`,{
      method:'POST',
      headers:{ 'Content-Type':'application/json' },
      body: JSON.stringify({ role, email, senha })
    });

    if(!resp.ok){
      throw new Error('E-mail ou senha inválidos.');
    }

    const data = await resp.json();
    localStorage.setItem('user', JSON.stringify(data));

    if (data.role === 'ALUNO')       location.href = 'aluno.html';
    else if (data.role === 'PROFESSOR') location.href = 'professor.html';
    else if (data.role === 'EMPRESA')   location.href = 'empresa.html';
    else showToast('error', 'Papel de usuário desconhecido.');
  }catch(e){
    showToast('error', e.message || 'Erro ao fazer login.');
  }finally{
    btn.classList.remove('is-loading');
    btn.textContent = originalText;
  }
}

document.getElementById('btnLogin').addEventListener('click', login);

['email','senha'].forEach(id=>{
  document.getElementById(id).addEventListener('keydown', (e)=>{
    if(e.key === 'Enter'){ login(); }
  });
});

/* =========================================
   ESQUECI MINHA SENHA – NOVA FUNCIONALIDADE
   ========================================= */

const btnEsqueciSenha   = document.getElementById('btnEsqueciSenha');
const forgotModal       = document.getElementById('forgotModal');
const btnCancelarReset  = document.getElementById('btnCancelarReset');
const btnConfirmarReset = document.getElementById('btnConfirmarReset');

if (btnEsqueciSenha && forgotModal) {
  btnEsqueciSenha.addEventListener('click', () => {
    forgotModal.classList.add('is-open');
    forgotModal.setAttribute('aria-hidden', 'false');
  });
}

if (btnCancelarReset && forgotModal) {
  btnCancelarReset.addEventListener('click', () => {
    forgotModal.classList.remove('is-open');
    forgotModal.setAttribute('aria-hidden', 'true');
  });
}

if (btnConfirmarReset) {
  btnConfirmarReset.addEventListener('click', async () => {
    const role          = document.getElementById('forgotRole').value;
    const email         = document.getElementById('forgotEmail').value.trim();
    const novaSenha     = document.getElementById('newPassword').value;
    const confirmaSenha = document.getElementById('confirmPassword').value;

    if (!email || !novaSenha || !confirmaSenha) {
      showToast('warning', 'Preencha todos os campos.');
      return;
    }

    if (novaSenha !== confirmaSenha) {
      showToast('warning', 'A confirmação de senha não confere.');
      return;
    }

    try {
      const resp = await fetch(`${API_BASE_URL}/auth/reset-password`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ role, email, novaSenha })
      });

      if (!resp.ok) {
        const text = await resp.text();
        console.error(text);
        showToast('error', 'Não foi possível redefinir a senha. Verifique o e-mail e o tipo de usuário.');
        return;
      }

      showToast('success', 'Senha redefinida com sucesso! Já pode fazer login com a nova senha.');
      forgotModal.classList.remove('is-open');
      forgotModal.setAttribute('aria-hidden', 'true');
    } catch (err) {
      console.error(err);
      showToast('error', 'Erro ao conectar com o servidor. Tente novamente mais tarde.');
    }
  });
}
