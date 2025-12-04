const user = JSON.parse(localStorage.getItem('user') || 'null');
if (!user || user.role !== 'EMPRESA') {
  location.href = 'login.html';
}
const empresaId = Number(user.id);
console.log('[EMPRESA] API_BASE_URL:', API_BASE_URL, 'empresaId:', empresaId);

const $ = (id) => document.getElementById(id);

function toSingular(path) {
  return path.replace('/empresas/', '/empresa/');
}

async function apiFetch(path, options = {}) {
  const urlPlural = `${API_BASE_URL}${path}`;
  console.log('[fetch] →', urlPlural, options);
  let r;
  try {
    r = await fetch(urlPlural, options);
  } catch (e) {
    console.error('[fetch] ERRO de rede:', e);
    throw e;
  }

  if (r.status === 404 && path.startsWith('/empresas/')) {
    const urlSing = `${API_BASE_URL}${toSingular(path)}`;
    console.warn('404 em plural, tentando singular:', urlSing);
    r = await fetch(urlSing, options);
  }
  console.log('[fetch] ←', r.status, r.statusText);
  return r;
}

/* ========== SALDO ========== */
async function carregarSaldo() {
  try {
    const r = await apiFetch(`/empresas/${empresaId}/wallet`);
    if (!r.ok) throw new Error(`Falha ao buscar saldo (${r.status})`);
    const d = await r.json();
    console.log('[saldo]', d);
    $('saldo').textContent = Number(d.saldo ?? 0).toFixed(2);
  } catch (e) {
    console.error('[saldo] erro:', e);
    $('saldo').textContent = '—';
  }
}

/* ========== HISTÓRICO ========== */
async function carregarHistorico() {
  try {
    const r = await apiFetch(`/empresas/${empresaId}/ledger`);
    if (!r.ok) throw new Error(`Falha ao buscar histórico (${r.status})`);
    const items = await r.json();
    console.log('[ledger]', items);

    const ul = $('hist');
    ul.innerHTML = '';

    const arr = Array.isArray(items) ? items : [];
    arr.forEach(i => {
      const li = document.createElement('li');
      li.textContent = `${i.ts} • ${i.kind} • ${i.amount} • ${i.reason || ''}`;
      ul.appendChild(li);
    });
  } catch (e) {
    console.error('[ledger] erro:', e);
    $('hist').innerHTML = '<li>Erro ao carregar histórico.</li>';
  }
}

/* ========== LISTAR BENEFÍCIOS ========== */
async function listarBeneficios() {
  try {
    const r = await apiFetch(`/empresas/${empresaId}/beneficios`);
    if (!r.ok) throw new Error(`Falha ao listar benefícios (${r.status})`);
    const items = await r.json();
    console.log('[beneficios]', items);

    const list = $('lista-beneficios');
    list.innerHTML = '';

    const arr = Array.isArray(items) ? items : [];
    if (arr.length === 0) {
      list.innerHTML = '<li>Nenhum benefício cadastrado.</li>';
      return;
    }

    arr.forEach(b => {
      const li = document.createElement('li');

      const card = document.createElement('article');
      card.className = 'benefit-card';

      const thumb = document.createElement('div');
      thumb.className = 'benefit-thumb';

      const img = document.createElement('img');
      img.className = 'benefit-img';

      const imgUrl = `${API_BASE_URL}/beneficios/${b.id}/image`;
      console.log('[benefit img]', b.id, '→', imgUrl);
      img.src = imgUrl;
      img.alt = b.titulo || 'Imagem do benefício';

      img.onerror = () => {
        console.warn('[benefit img] erro ao carregar imagem do benefício', b.id);
        // Se quiser esconder o quadradinho quando der erro:
        // img.style.display = 'none';
      };

      thumb.appendChild(img);

      const body = document.createElement('div');
      body.className = 'benefit-body';

      const h3 = document.createElement('h3');
      h3.textContent = b.titulo;

      const p = document.createElement('p');
      p.className = 'benefit-description';
      p.textContent = b.descricao || 'Sem descrição.';

      const span = document.createElement('span');
      span.className = 'benefit-cost';
      span.textContent = `${b.custo} moedas`;

      body.appendChild(h3);
      body.appendChild(p);
      body.appendChild(span);

      card.appendChild(thumb);
      card.appendChild(body);
      li.appendChild(card);
      list.appendChild(li);
    });
  } catch (e) {
    console.error('[beneficios] erro:', e);
    $('lista-beneficios').innerHTML = '<li>Erro ao carregar benefícios.</li>';
  }
}

/* ========== CRIAR BENEFÍCIO ========== */
async function criarBeneficio() {
  try {
    const titulo = $('titulo')?.value?.trim() || '';
    const descricao = $('descricao')?.value?.trim() || '';
    const custo = parseInt($('custo')?.value || '0', 10);
    const fotoFile = $('foto')?.files?.[0] || null;

    if (!titulo || !custo) {
      alert('Informe título e custo.');
      return;
    }

    let r;

    if (fotoFile) {
      // Com foto → usa endpoint multipart
      const fd = new FormData();
      fd.append('titulo', titulo);
      fd.append('descricao', descricao);
      fd.append('custo', String(custo));
      fd.append('foto', fotoFile);

      r = await apiFetch(`/empresas/${empresaId}/beneficios/upload`, {
        method: 'POST',
        body: fd
      });
    } else {
      // Sem foto → usa endpoint JSON antigo (continua funcionando)
      r = await apiFetch(`/empresas/${empresaId}/beneficios`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ titulo, descricao, custo })
      });
    }

    const txt = await r.text().catch(() => '');
    if (!r.ok) throw new Error(`Falha ao criar benefício (${r.status}): ${txt}`);

    $('titulo').value = '';
    $('descricao').value = '';
    $('custo').value = '';
    if ($('foto')) $('foto').value = '';

    await listarBeneficios();
    alert('Benefício criado com sucesso!');
  } catch (e) {
    console.error('[criar beneficio] erro:', e);
    alert(e.message || 'Erro ao criar benefício');
  }
}

/* ========== ENVIAR MOEDAS ========== */
function parseIntSafe(id) {
  const v = ($(id)?.value ?? '').trim();
  const n = parseInt(v, 10);
  return Number.isFinite(n) ? n : 0;
}

async function enviarMoedas(e) {
  e?.preventDefault?.();

  const professorId = parseIntSafe('professorId');
  const amount      = parseIntSafe('quantidade');
  const reason      = ($('motivo')?.value || '').trim();

  if (!professorId || !amount) {
    alert('Informe o ID do professor e a quantidade de moedas.');
    return;
  }

  try {
    const r = await apiFetch(`/empresas/${empresaId}/grant`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ professorId, amount, reason })
    });

    const txt = await r.text().catch(() => '');
    if (!r.ok) throw new Error(`Falha ao enviar moedas (${r.status}): ${txt}`);

    alert('Moedas enviadas com sucesso!');
    await carregarSaldo();
    await carregarHistorico();
  } catch (e2) {
    console.error('[grant] erro:', e2);
    alert(e2.message || 'Erro ao enviar moedas');
  }
}

/* ========== BIND BOTÕES / INICIALIZAÇÃO ========== */
$('criar').onclick = criarBeneficio;
$('enviar').addEventListener('click', enviarMoedas);
$('sair').onclick = () => {
  localStorage.removeItem('user');
  location.href = 'login.html';
};

carregarSaldo().catch(console.warn);
carregarHistorico().catch(console.warn);
listarBeneficios().catch(console.warn);
