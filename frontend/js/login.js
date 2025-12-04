async function login(){
  const role  = document.getElementById('role').value;
  const email = document.getElementById('email').value.trim();
  const senha = document.getElementById('senha').value.trim();
  if(!email || !senha){ alert('Preencha e-mail e senha.'); return; }

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

    if(!resp.ok){ throw new Error('Login inválido'); }

    const data = await resp.json();
    localStorage.setItem('user', JSON.stringify(data));

    if (data.role === 'ALUNO')       location.href = 'aluno.html';
    else if (data.role === 'PROFESSOR') location.href = 'professor.html';
    else if (data.role === 'EMPRESA')   location.href = 'empresa.html';
    else alert('Papel desconhecido.');
  }catch(e){
    alert(e.message || 'Erro ao fazer login.');
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
