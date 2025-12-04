const user = JSON.parse(localStorage.getItem('user') || 'null');
if (!user || user.role !== 'ALUNO') {
  location.href = 'login.html';
}
const id = Number(user.id);

const $ = (id) => document.getElementById(id);

/* ========= POPUP CUSTOM ========= */

const popupResgate = $('popup-resgate');
const popupTitleEl = $('popup-title');
const popupBodyEl  = $('popup-body');
const popupOkBtn   = $('popup-ok');

function showPopup(title, message) {
  if (!popupResgate || !popupTitleEl || !popupBodyEl) {
    alert(message);
    return;
  }
  popupTitleEl.textContent = title;
  popupBodyEl.innerHTML = message.replace(/\n/g, '<br>');
  popupResgate.classList.remove('hidden');
}

if (popupOkBtn && popupResgate) {
  popupOkBtn.onclick = () => {
    popupResgate.classList.add('hidden');
  };
}

/* ========= PERFIL ========= */

async function loadPerfil() {
  try {
    const r = await fetch(`${API_BASE_URL}/alunos/${id}`);
    if (!r.ok) {
      console.error('Falha ao buscar perfil', r.status, await r.text().catch(() => ''));
      return;
    }
    const a = await r.json();
    $('nome').value  = a?.nome  ?? '';
    $('curso').value = a?.curso ?? '';
    $('email').value = a?.email ?? '';
  } catch (e) {
    console.error('Erro loadPerfil', e);
  }
}

async function salvar() {
  try {
    const body = {
      nome:  $('nome').value,
      curso: $('curso').value,
      email: $('email').value
    };
    const r = await fetch(`${API_BASE_URL}/alunos/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
    if (!r.ok) {
      const txt = await r.text().catch(() => '');
      showPopup('Erro ao salvar', `Erro ao salvar (${r.status}): ${txt}`);
      return;
    }
    showPopup('Sucesso', 'Dados atualizados!');
  } catch (e) {
    console.error(e);
    showPopup('Erro', 'Falha ao salvar.');
  }
}

async function excluir() {
  try {
    if (!confirm('Tem certeza? Esta ação é irreversível.')) return;
    const r = await fetch(`${API_BASE_URL}/alunos/${id}`, { method: 'DELETE' });
    if (!r.ok) {
      const txt = await r.text().catch(() => '');
      showPopup('Erro ao excluir', `Erro ao excluir (${r.status}): ${txt}`);
      return;
    }
    localStorage.removeItem('user');
    location.href = 'login.html';
  } catch (e) {
    console.error(e);
    showPopup('Erro', 'Falha ao excluir conta.');
  }
}

/* ========= SALDO / HISTÓRICO ========= */

async function loadSaldo() {
  try {
    const r = await fetch(`${API_BASE_URL}/alunos/${id}/wallet`);
    if (!r.ok) {
      console.error('Falha ao buscar saldo', r.status, await r.text().catch(() => ''));
      $('saldo').textContent = '—';
      return;
    }
    const d = await r.json();
    $('saldo').textContent = Number(d?.saldo ?? 0).toFixed(2);
  } catch (e) {
    console.error(e);
    $('saldo').textContent = '—';
  }
}

async function loadHist() {
  try {
    const r = await fetch(`${API_BASE_URL}/alunos/${id}/ledger`);
    const ul = $('hist');

    if (!r.ok) {
      console.error('Falha ao buscar histórico', r.status, await r.text().catch(() => ''));
      ul.innerHTML = '<li>Erro ao carregar histórico.</li>';
      return;
    }

    const items = await r.json();
    const arr = Array.isArray(items) ? items : [];

    ul.innerHTML = '';
    arr.forEach(i => {
      const li = document.createElement('li');
      li.textContent = `${i.ts} • ${i.kind} • ${i.amount} • ${i.reason || ''}`;
      ul.appendChild(li);
    });
  } catch (e) {
    console.error(e);
    $('hist').innerHTML = '<li>Erro ao carregar histórico.</li>';
  }
}

/* ========= BENEFÍCIOS ========= */

async function loadBeneficios() {
  try {
    const r = await fetch(`${API_BASE_URL}/beneficios`);
    if (!r.ok) {
      console.error('Falha ao listar benefícios', r.status, await r.text().catch(() => ''));
      $('beneficios').innerHTML = '<li>Erro ao carregar benefícios.</li>';
      return;
    }

    const items = await r.json();
    const arr = Array.isArray(items) ? items : [];
    const ul = $('beneficios');
    ul.innerHTML = '';

    if (arr.length === 0) {
      ul.innerHTML = '<li>Nenhum benefício disponível.</li>';
      return;
    }

    arr.forEach(b => {
      const li = document.createElement('li');
      li.className = 'benefit-card';

      li.dataset.titulo = b.titulo;
      li.dataset.custo  = b.custo;

      li.innerHTML = `
        <div class="benefit-thumb">
          <img
            src="${API_BASE_URL}/beneficios/${b.id}/image"
            alt="${b.titulo}"
            class="benefit-img"
            onerror="this.style.display='none'"
          >
        </div>
        <div class="benefit-title">${b.titulo}</div>
        <div class="benefit-description">
          ${b.descricao || 'Benefício disponível para troca de moedas.'}
        </div>
        <div class="benefit-footer">
          <span class="benefit-cost-badge">${b.custo} moedas</span>
          <button class="benefit-redeem-btn" data-id="${b.id}">Resgatar</button>
        </div>
      `;
      ul.appendChild(li);
    });

    ul.onclick = async (e) => {
      const btn = e.target.closest('button[data-id]');
      if (!btn) return;

      const benId = btn.getAttribute('data-id');
      const li = btn.closest('li');

      try {
        const r2 = await fetch(`${API_BASE_URL}/alunos/${id}/redeem/${benId}`, {
          method: 'POST'
        });

        const txt = await r2.text().catch(() => '');
        console.log('[redeem raw response]', txt);

        if (!r2.ok) {
          showPopup('Erro ao resgatar', `Falha ao resgatar (${r2.status}): ${txt}`);
          return;
        }

        let data = {};
        if (txt) {
          try {
            data = JSON.parse(txt);
          } catch (e) {
            console.error('Falha ao parsear JSON de redeem', e);
          }
        }

        const code      = data.code ?? '—';
        const expiresAt = data.expiresAt
          ? new Date(data.expiresAt).toLocaleString('pt-BR')
          : '—';
        const tituloApi = data.benefitTitle ?? 'Benefício';

        const benefitTitle = li?.dataset.titulo || tituloApi;
        const benefitCost  = li?.dataset.custo  || 0;

        const popupMsg =
          `${benefitTitle} resgatado com sucesso!\n\n` +
          `Código: ${code}\n` +
          `Válido até: ${expiresAt}`;

        showPopup('Resgate realizado!', popupMsg);

        /* ===== QR CODE ===== */
        try {
          const qrSection = document.getElementById('qr-section');
          const qrInfo    = document.getElementById('qr-info');
          const qrBox     = document.getElementById('qrcode');
          const qrExtra   = document.getElementById('qr-extra-msg');

          if (qrSection && qrInfo && qrBox && window.QRCode) {
            qrSection.classList.remove('hidden');

            qrInfo.textContent =
              `${benefitTitle} • Código: ${code} • Válido até: ${expiresAt}`;

            qrBox.innerHTML = '';

            new QRCode(qrBox, {
              text: code,
              width: 190,
              height: 190
            });

            if (qrExtra) {
              qrExtra.innerHTML =
                `<b>${benefitTitle}</b> resgatado com sucesso!<br>` +
                `Código: <b>${code}</b><br>` +
                `Válido até: <b>${expiresAt}</b>`;
            }
          } else {
            console.warn('QR Code não pôde ser gerado (elementos ou lib ausentes).');
          }
        } catch (qrErr) {
          console.error('Erro ao gerar QR Code', qrErr);
        }

        /* ===== EMAILJS (com imagem do benefício) ===== */
        try {
          if (window.emailjs) {
            const alunoNome  = $('nome').value || (user && user.nome) || 'Aluno';
            const alunoEmail = $('email').value || (user && user.email);

            // URL da imagem do benefício para o e-mail
            const imageUrl = `${API_BASE_URL}/beneficios/${benId}/image`;

            if (alunoEmail) {
              await emailjs.send(
                'service_q59jvqh',   // service_id
                'template_z1cp5jv',  // template_id
                {
                  aluno_nome: alunoNome,
                  moedas: benefitCost,
                  beneficio: benefitTitle,
                  codigo: code,
                  name: alunoNome,
                  email: alunoEmail,

                  // *** variável usada no template do EmailJS ***
                  imagem_url: imageUrl
                }
              );
              console.log('EmailJS: e-mail enviado com sucesso');
            } else {
              console.warn('EmailJS: aluno sem e-mail cadastrado, não foi possível enviar.');
            }
          } else {
            console.warn('EmailJS não está carregado na página.');
          }
        } catch (emailErr) {
          console.error('Erro ao enviar e-mail via EmailJS', emailErr);
        }

        if (li) li.remove();

        await loadSaldo();
        await loadHist();
      } catch (err) {
        console.error(err);
        showPopup('Erro', 'Erro ao resgatar benefício.');
      }
    };

  } catch (e) {
    console.error(e);
    $('beneficios').innerHTML = '<li>Erro ao carregar benefícios.</li>';
  }
}

/* ========= MODAL DO HISTÓRICO ========= */

const historicoModal = document.getElementById('historicoModal');
const backdrop = historicoModal ? historicoModal.querySelector('.modal-backdrop') : null;

function abrirHistorico() {
  if (historicoModal) historicoModal.classList.remove('hidden');
}

function fecharHistorico() {
  if (historicoModal) historicoModal.classList.add('hidden');
}

/* ========= BIND / INIT ========= */

$('salvar').onclick = salvar;
$('excluir').onclick = excluir;
$('sair').onclick = () => {
  localStorage.removeItem('user');
  location.href = 'login.html';
};

const btnHist = document.getElementById('btn-hist');
if (btnHist) {
  btnHist.onclick = async () => {
    await loadHist();
    abrirHistorico();
  };
}

const fecharBtn = document.getElementById('fecharHistorico');
if (fecharBtn) fecharBtn.onclick = fecharHistorico;
if (backdrop) backdrop.onclick = fecharHistorico;

/* === Navegação do carrossel de benefícios (se os botões existirem) === */
const beneficiosList = document.getElementById('beneficios');
const btnBenefLeft   = document.getElementById('benef-left');
const btnBenefRight  = document.getElementById('benef-right');

if (beneficiosList && btnBenefLeft && btnBenefRight) {
  const step = 280;

  btnBenefLeft.onclick = () => {
    beneficiosList.scrollBy({ left: -step, behavior: 'smooth' });
  };

  btnBenefRight.onclick = () => {
    beneficiosList.scrollBy({ left: step, behavior: 'smooth' });
  };
}

loadPerfil();
loadSaldo();
loadHist();
loadBeneficios();
