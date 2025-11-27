document.getElementById('btn').onclick=async()=>{
  const cnpj=document.getElementById('cnpj').value.trim();
  const nome=document.getElementById('nome').value.trim();
  const email=document.getElementById('email').value.trim();
  const senha=document.getElementById('senha').value.trim();
  if(!cnpj||!nome||!email||!senha){alert('Preencha todos os campos.');return;}
  const resp=await fetch(`${API_BASE_URL}/auth/empresa/register`,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({cnpj,nome,email,senha})});
  if(!resp.ok){alert('Erro no cadastro.');return;} alert('Cadastro realizado! Faça login.'); location.href='login.html';
};