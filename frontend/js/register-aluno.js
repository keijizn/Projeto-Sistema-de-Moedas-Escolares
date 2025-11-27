document.getElementById('btn').onclick=async()=>{
  const nome=document.getElementById('nome').value.trim();
  const curso=document.getElementById('curso').value.trim();
  const email=document.getElementById('email').value.trim();
  const cpf=document.getElementById('cpf').value.trim();
  const senha=document.getElementById('senha').value.trim();
  if(!nome||!curso||!email||!cpf||!senha){alert('Preencha todos os campos.');return;}
  const resp=await fetch(`${API_BASE_URL}/auth/aluno/register`,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({nome,curso,email,cpf,senha})});
  if(!resp.ok){alert('Erro no cadastro.');return;} alert('Cadastro realizado! Faça login.'); location.href='login.html';
};