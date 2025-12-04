const user = JSON.parse(localStorage.getItem('user') || 'null');
if (!user || user.role !== 'PROFESSOR') {
  location.href = 'login.html';
}
const professorId = Number(user.id);

const $ = (id) => document.getElementById(id);

function toSingular(path) {
  return path.replace('/professores/', '/professor/');
}
async function apiFetch(path, options = {}) {
  const url = `${API_BASE_URL}${path}`;
  let r = await fetch(url, options);

  if (r.status === 404 && path.startsWith('/professores/')) {
    const urlSing = `${API_BASE_URL}${toSingular(path)}`;
    console.warn('404 em plural, tentando singular:', urlSing);
    r = await fetch(urlSing, options);
  }
  return r;
}

async function enviar() {
  try {
    const alunoId    = Number($('alunoId')?.value || 0);
    const quantidade = Number($('quantidade')?.value || 0);
    const motivo     = $('motivo')?.value?.trim() || '';

    if (!alunoId || !quantidade || quantidade <= 0) {
      alert('Informe o ID do aluno e uma quantidade > 0.');
      return;
    }

    const res = await apiFetch(`/professores/${professorId}/grant`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ alunoId, amount: quantidade, reason: motivo })
    });

    if (!res.ok) {
      const txt = await res.text().catch(() => '');
      console.error('Grant falhou:', res.status, txt);
      alert(`Falha ao enviar (${res.status}): ${txt}`);
      return;
    }

    alert('Moedas enviadas!');
    await loadSaldo();
    await loadHist();
  } catch (e) {
    console.error('Erro no fetch/grant:', e);
    alert(e?.message || 'Erro ao enviar moedas');
  }
}

async function loadSaldo() {
  const r = await apiFetch(`/professores/${professorId}/wallet`);
  if (!r.ok) throw new Error('Falha ao buscar saldo');
  const d = await r.json();
  $('saldo').textContent = Number(d.saldo ?? 0).toFixed(2);
}

async function loadHist() {
  const r = await apiFetch(`/professores/${professorId}/ledger`);
  if (!r.ok) throw new Error('Falha ao buscar histórico');
  const items = await r.json();
  const ul = $('hist');
  ul.innerHTML = '';

  (Array.isArray(items) ? items : []).forEach(i => {
    const li = document.createElement('li');
    li.textContent = `${i.ts} • ${i.kind} • ${i.amount} • ${i.reason || ''}`;
    ul.appendChild(li);
  });
}

$('enviar').onclick = enviar;
$('sair').onclick = () => {
  localStorage.removeItem('user');
  location.href = 'login.html';
};

loadSaldo().catch(console.warn);
loadHist().catch(console.warn);
