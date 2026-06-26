const API_BASE = '/api';
const state = {
  token: localStorage.getItem('collectorhub_token') || '',
  user: null,
  profile: null,
  types: [],
  collectibles: [],
  selectedCollectible: null,
  releases: [],
  reviews: [],
  activeType: 'all'
};

const $ = (selector) => document.querySelector(selector);
const els = {
  userBadge: $('#userBadge'),
  logoutBtn: $('#logoutBtn'),
  typeList: $('#typeList'),
  collectibleGrid: $('#collectibleGrid'),
  releaseList: $('#releaseList'),
  reviewFeed: $('#reviewFeed'),
  selectedArtwork: $('#selectedArtwork'),
  selectedName: $('#selectedName'),
  selectedMeta: $('#selectedMeta'),
  selectedStats: $('#selectedStats'),
  featuredTitle: $('#featuredTitle'),
  featuredPrice: $('#featuredPrice'),
  featuredStock: $('#featuredStock'),
  featuredScore: $('#featuredScore'),
  releaseStatus: $('#releaseStatus'),
  authStatus: $('#authStatus'),
  reviewStatus: $('#reviewStatus'),
  reviewCollectibleSelect: $('#reviewCollectibleSelect'),
  loginPanel: $('#loginPanel'),
  profilePanel: $('#profilePanel'),
  profileIcon: $('#profileIcon'),
  profileName: $('#profileName'),
  profileIntro: $('#profileIntro'),
  profileFans: $('#profileFans'),
  profileFollowee: $('#profileFollowee'),
  profileCredits: $('#profileCredits'),
  profileDetails: $('#profileDetails'),
  editProfileBtn: $('#editProfileBtn'),
  profileLogoutBtn: $('#profileLogoutBtn'),
  profileEditForm: $('#profileEditForm'),
  editNickName: $('#editNickName'),
  editIcon: $('#editIcon'),
  editIconFile: $('#editIconFile'),
  editIconPreview: $('#editIconPreview'),
  avatarUploadStatus: $('#avatarUploadStatus'),
  editCity: $('#editCity'),
  editIntroduce: $('#editIntroduce'),
  editGender: $('#editGender'),
  editBirthday: $('#editBirthday'),
  cancelEditProfileBtn: $('#cancelEditProfileBtn'),
  profileEditStatus: $('#profileEditStatus'),
  toast: $('#toast')
};

const TXT = {
  noInfo: '\u6682\u65e0\u4fe1\u606f',
  toyImage: '\u6f6e\u73a9\u56fe\u7247',
  loginFirst: '\u8bf7\u5148\u767b\u5f55\u540e\u518d\u64cd\u4f5c',
  requestFailed: '\u8bf7\u6c42\u5931\u8d25',
  backendFailed: '\u540e\u7aef\u8fd4\u56de\u5931\u8d25',
  player: '\u73a9\u5bb6',
  guest: '\u672a\u767b\u5f55',
  profileFallbackIntro: '\u8fd9\u4f4d\u73a9\u5bb6\u8fd8\u6ca1\u6709\u7559\u4e0b\u4e2a\u4eba\u4ecb\u7ecd\u3002',
  profileEmpty: '\u767b\u5f55\u540e\u53ef\u67e5\u770b\u4e2a\u4eba\u8d44\u6599\u3002',
  phone: '\u624b\u673a\u53f7',
  city: '\u57ce\u5e02',
  gender: '\u6027\u522b',
  birthday: '\u751f\u65e5',
  level: '\u4f1a\u5458',
  joinedAt: '\u52a0\u5165\u65f6\u95f4',
  male: '\u7537',
  female: '\u5973',
  enabled: '\u5df2\u5f00\u901a',
  disabled: '\u672a\u5f00\u901a',
  editProfile: '\u7f16\u8f91\u8d44\u6599',
  closeEdit: '\u6536\u8d77\u7f16\u8f91',
  profileSaving: '\u6b63\u5728\u4fdd\u5b58\u8d44\u6599...',
  profileSaved: '\u8d44\u6599\u5df2\u4fdd\u5b58',
  avatarUploading: '\u6b63\u5728\u4e0a\u4f20\u5934\u50cf...',
  avatarUploaded: '\u5934\u50cf\u5df2\u4e0a\u4f20',
  chooseAvatar: '\u8bf7\u5148\u9009\u62e9\u5934\u50cf\u56fe\u7247',
  allToys: '\u5168\u90e8\u5355\u54c1',
  unnamedType: '\u672a\u547d\u540d\u54c1\u7c7b',
  unnamedToy: '\u672a\u547d\u540d\u5355\u54c1',
  online: '\u7ebf\u4e0a\u53d1\u552e',
  review: '\u6d4b\u8bc4',
  loadingToys: '\u6b63\u5728\u52a0\u8f7d\u6f6e\u73a9\u5355\u54c1...',
  noToys: '\u6682\u65e0\u5355\u54c1\u6570\u636e\u3002\u8bf7\u786e\u8ba4\u540e\u7aef\u548c\u6570\u636e\u5e93\u793a\u4f8b\u6570\u636e\u5df2\u7ecf\u542f\u52a8\u3002',
  chooseToy: '\u9009\u62e9\u4e00\u4e2a\u5355\u54c1',
  detailHint: '\u67e5\u770b\u4ef7\u683c\u3001\u6e20\u9053\u3001\u8bc4\u5206\u548c\u5173\u8054\u53d1\u552e\u3002',
  allDay: '\u5168\u5929\u5019\u5173\u6ce8',
  price: '\u53c2\u8003\u4ef7',
  sold: '\u5df2\u552e',
  score: '\u8bc4\u5206',
  drop: '\u6f6e\u73a9\u53d1\u552e',
  selectRelease: '\u9009\u62e9\u4e00\u4e2a\u5355\u54c1\u540e\u5c55\u793a\u5b83\u7684\u666e\u901a\u53d1\u552e\u548c\u9650\u91cf\u79d2\u6740\u3002',
  noRelease: '\u8be5\u5355\u54c1\u6682\u65e0\u53d1\u552e\u4fe1\u606f\u3002',
  noReleaseShort: '\u6682\u65e0\u53d1\u552e',
  releases: '\u4e2a\u53d1\u552e',
  unlimited: '\u4e0d\u9650',
  unnamedRelease: '\u672a\u547d\u540d\u53d1\u552e',
  followOfficial: '\u5173\u6ce8\u5b98\u65b9\u6e20\u9053\u83b7\u5f97\u66f4\u591a\u53d1\u552e\u4fe1\u606f',
  rules: '\u6309\u5e73\u53f0\u89c4\u5219\u8d2d\u4e70',
  original: '\u539f\u4ef7',
  rush: '\u7acb\u5373\u62a2\u8d2d',
  normalSale: '\u666e\u901a\u53d1\u552e',
  noReviews: '\u6682\u65e0\u6d4b\u8bc4\u3002\u767b\u5f55\u540e\u53d1\u5e03\u7b2c\u4e00\u7bc7\u5f00\u7bb1\u8bb0\u5f55\u3002',
  chPlayer: 'CollectorHub \u73a9\u5bb6',
  untitledReview: '\u65e0\u6807\u9898\u6d4b\u8bc4',
  noReviewContent: '\u8fd9\u4f4d\u73a9\u5bb6\u8fd8\u6ca1\u6709\u7559\u4e0b\u8be6\u7ec6\u5185\u5bb9\u3002',
  like: '\u559c\u6b22',
  likedUsers: '\u70b9\u8d5e\u73a9\u5bb6',
  comments: '\u8bc4\u8bba',
  phoneRequired: '\u8bf7\u8f93\u5165\u624b\u673a\u53f7\u3002',
  codeSent: '\u9a8c\u8bc1\u7801\u5df2\u53d1\u9001\u3002\u5f00\u53d1\u73af\u5883\u53ef\u67e5\u770b\u540e\u7aef\u65e5\u5fd7\u3002',
  loginOk: '\u767b\u5f55\u6210\u529f\u3002',
  logoutOk: '\u5df2\u9000\u51fa\u767b\u5f55',
  orderSubmitted: '\u62a2\u8d2d\u8bf7\u6c42\u5df2\u63d0\u4ea4\uff0c\u8ba2\u5355\u53f7\uff1a',
  recentLikes: '\u6700\u8fd1\u559c\u6b22\uff1a',
  noLikes: '\u8fd8\u6ca1\u6709\u73a9\u5bb6\u70b9\u8d5e',
  reviewRequired: '\u8bf7\u9009\u62e9\u5173\u8054\u5355\u54c1\uff0c\u5e76\u586b\u5199\u6807\u9898\u548c\u5185\u5bb9\u3002',
  noSelectableCollectible: '\u6682\u65e0\u53ef\u9009\u62e9\u5355\u54c1',
  publishing: '\u6b63\u5728\u53d1\u5e03\u6d4b\u8bc4...',
  published: '\u6d4b\u8bc4\u5df2\u53d1\u5e03\uff0c\u7f16\u53f7',
  loadingDrops: '\u6b63\u5728\u52a0\u8f7d\u53d1\u552e\u65e5\u5386...',
  loadingReviews: '\u6b63\u5728\u52a0\u8f7d\u73a9\u5bb6\u6d4b\u8bc4...'
};

function money(cents) {
  if (cents === null || cents === undefined || Number.isNaN(Number(cents))) return '--';
  return '\u00a5' + (Number(cents) / 100).toFixed(0);
}
function cleanText(value, fallback = TXT.noInfo) { return value === null || value === undefined || value === '' ? fallback : String(value); }
function escapeHtml(value) { return cleanText(value, '').replace(/[&<>"']/g, (char) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[char])); }
function imageUrl(path) {
  if (!path) return './imgs/collectibles/star-bear.png';
  const first = String(path).split(',')[0].trim();
  if (/^https?:\/\//i.test(first)) return first;
  if (first.startsWith('/imgs/')) return first;
  if (first.startsWith('/reviews/')) return '/imgs' + first;
  if (first.startsWith('/types/')) return first;
  return first.startsWith('/') ? first : '/imgs/' + first;
}
function showView(name, updateHash = true) {
  const next = document.querySelector('[data-view="' + name + '"]') ? name : 'discover';
  document.querySelectorAll('.view').forEach((view) => view.classList.toggle('active', view.dataset.view === next));
  const activeTab = next === 'profile-edit' ? 'profile' : next;
  document.querySelectorAll('.bottom-tabs button').forEach((button) => button.classList.toggle('active', button.dataset.target === activeTab));
  if (next === 'profile-edit') fillProfileEditForm();
  if (updateHash) history.replaceState(null, '', '#' + next);
  window.scrollTo({ top: 0, behavior: 'smooth' });
}
function showToast(message) {
  els.toast.textContent = message;
  els.toast.classList.add('show');
  window.clearTimeout(showToast.timer);
  showToast.timer = window.setTimeout(() => els.toast.classList.remove('show'), 2800);
}
async function api(path, options = {}) {
  const headers = new Headers(options.headers || {});
  if (!(options.body instanceof FormData)) headers.set('Content-Type', 'application/json');
  if (state.token) headers.set('authorization', state.token);
  const response = await fetch(API_BASE + path, { ...options, headers });
  if (response.status === 401) throw new Error(TXT.loginFirst);
  const result = await response.json().catch(() => null);
  if (!response.ok) throw new Error(result?.errorMsg || TXT.requestFailed + '\uff1a' + response.status);
  if (result && result.success === false) throw new Error(result.errorMsg || TXT.backendFailed);
  return result ? result.data : null;
}
function setLoading(container, text) { container.innerHTML = '<div class="empty-state">' + escapeHtml(text) + '</div>'; }
function artwork(path, name, className = 'toy-image') {
  return '<div class="' + className + '"><img src="' + escapeHtml(imageUrl(path)) + '" alt="' + escapeHtml(cleanText(name, TXT.toyImage)) + '" data-fallback="./imgs/collectibles/star-bear.png"></div>';
}
function wireImageFallbacks(root = document) {
  root.querySelectorAll('img[data-fallback]').forEach((img) => {
    const replace = () => { if (!img.src.endsWith(img.dataset.fallback)) img.src = img.dataset.fallback; };
    img.addEventListener('error', replace, { once: true });
    if (img.complete && img.naturalWidth === 0) replace();
  });
}
function renderUser() {
  if (state.user) { els.userBadge.textContent = state.user.nickName || state.user.name || TXT.player + ' ' + state.user.id; els.logoutBtn.hidden = false; return; }
  els.userBadge.textContent = TXT.guest;
  els.logoutBtn.hidden = true;
}
function displayValue(value) { return value === null || value === undefined || value === '' ? '--' : String(value); }
function formatProfileGender(value) { if (value === true) return TXT.male; if (value === false) return TXT.female; return '--'; }
function formatProfileLevel(value) { if (value === true) return TXT.enabled; if (value === false) return TXT.disabled; return '--'; }
function renderProfileDetail(label, value) { return '<div><span>' + escapeHtml(label) + '</span><strong>' + escapeHtml(displayValue(value)) + '</strong></div>'; }
function renderProfile() {
  const loggedIn = Boolean(state.token && state.user);
  if (els.loginPanel) els.loginPanel.hidden = loggedIn;
  if (els.profilePanel) els.profilePanel.hidden = !loggedIn;
  if (!loggedIn) return;
  const profile = state.profile || {};
  const name = profile.nickName || state.user.nickName || TXT.player + ' ' + state.user.id;
  els.profileName.textContent = name;
  els.profileIntro.textContent = profile.introduce || TXT.profileFallbackIntro;
  els.profileFans.textContent = displayValue(profile.fans);
  els.profileFollowee.textContent = displayValue(profile.followee);
  els.profileCredits.textContent = displayValue(profile.credits);
  els.profileIcon.src = imageUrl(profile.icon || state.user.icon);
  els.profileDetails.innerHTML = [
    renderProfileDetail(TXT.phone, profile.phone),
    renderProfileDetail(TXT.city, profile.city),
    renderProfileDetail(TXT.gender, formatProfileGender(profile.gender)),
    renderProfileDetail(TXT.birthday, profile.birthday),
    renderProfileDetail(TXT.level, formatProfileLevel(profile.level)),
    renderProfileDetail(TXT.joinedAt, profile.createTime ? String(profile.createTime).replace('T', ' ').slice(0, 16) : '')
  ].join('');
  if (els.editProfileBtn) els.editProfileBtn.textContent = TXT.editProfile;
  wireImageFallbacks(els.profilePanel);
}
function fillProfileEditForm() {
  const profile = state.profile || {};
  els.editNickName.value = profile.nickName || state.user?.nickName || '';
  els.editIcon.value = profile.icon || state.user?.icon || '';
  els.editIconFile.value = '';
  els.editIconPreview.src = imageUrl(els.editIcon.value);
  els.avatarUploadStatus.textContent = '';
  els.editCity.value = profile.city || '';
  els.editIntroduce.value = profile.introduce || '';
  els.editGender.value = profile.gender === true ? 'true' : (profile.gender === false ? 'false' : '');
  els.editBirthday.value = profile.birthday || '';
  els.profileEditStatus.textContent = '';
}
function openProfileEdit() {
  fillProfileEditForm();
  showView('profile-edit');
}
function closeProfileEdit() {
  showView('profile');
}
function renderTypes() {
  if (!state.types.length) {
    els.typeList.innerHTML = '<button class="type-chip ' + (state.activeType === 'all' ? 'active' : '') + '" type="button" data-type="all">' + TXT.allToys + '</button>';
    return;
  }
  els.typeList.innerHTML = '<button class="type-chip ' + (state.activeType === 'all' ? 'active' : '') + '" type="button" data-type="all">' + TXT.allToys + '</button>' +
    state.types.map((type) => '<button class="type-chip ' + (String(state.activeType) === String(type.id) ? 'active' : '') + '" type="button" data-type="' + type.id + '">' + escapeHtml(cleanText(type.name, TXT.unnamedType)) + '</button>').join('');
}
function renderCollectibles() {
  if (!state.collectibles.length) { els.collectibleGrid.innerHTML = '<div class="empty-state">' + TXT.noToys + '</div>'; return; }
  els.collectibleGrid.innerHTML = state.collectibles.map((item) => '<button class="toy-card ' + (state.selectedCollectible?.id === item.id ? 'active' : '') + '" type="button" data-id="' + item.id + '">' + artwork(item.images, item.name) + '<div class="toy-body"><div><h3>' + escapeHtml(cleanText(item.name, TXT.unnamedToy)) + '</h3><p class="muted">' + escapeHtml(cleanText(item.area, 'CollectorHub')) + ' \u00b7 ' + escapeHtml(cleanText(item.address, TXT.online)) + '</p></div><footer><span class="price">' + money(item.avgPrice) + '</span><span>' + (item.score ?? 0) + '/50 \u00b7 ' + (item.comments ?? 0) + ' ' + TXT.review + '</span></footer></div></button>').join('');
  wireImageFallbacks(els.collectibleGrid);
}
function renderSelected() {
  const item = state.selectedCollectible;
  if (!item) { els.selectedArtwork.innerHTML = '<img src="./imgs/collectibles/star-bear.png" alt="CollectorHub">'; return; }
  els.selectedArtwork.innerHTML = '<img src="' + escapeHtml(imageUrl(item.images)) + '" alt="' + escapeHtml(cleanText(item.name, TXT.toyImage)) + '" data-fallback="./imgs/collectibles/star-bear.png">';
  els.selectedName.textContent = cleanText(item.name, TXT.unnamedToy);
  els.selectedMeta.textContent = cleanText(item.area, 'CollectorHub') + ' \u00b7 ' + cleanText(item.address, TXT.online) + ' \u00b7 ' + cleanText(item.openHours, TXT.allDay);
  els.selectedStats.innerHTML = '<span><strong>' + money(item.avgPrice) + '</strong><small>' + TXT.price + '</small></span><span><strong>' + (item.sold ?? 0) + '</strong><small>' + TXT.sold + '</small></span><span><strong>' + (item.score ?? 0) + '/50</strong><small>' + TXT.score + '</small></span>';
  if (els.reviewCollectibleSelect) els.reviewCollectibleSelect.value = String(item.id);
  els.featuredTitle.textContent = cleanText(item.name, TXT.drop);
  els.featuredPrice.textContent = money(item.avgPrice);
  els.featuredScore.textContent = String(item.score ?? 0);
  wireImageFallbacks(document.querySelector('.product-detail'));
}
function formatCollectibleOption(item) { return cleanText(item.name, TXT.unnamedToy) + ' \u00b7 ' + cleanText(item.area, 'CollectorHub') + ' \u00b7 ' + money(item.avgPrice); }
function renderCollectibleOptions() {
  if (!els.reviewCollectibleSelect) return;
  if (!state.collectibles.length) {
    els.reviewCollectibleSelect.innerHTML = '<option value="">' + TXT.noSelectableCollectible + '</option>';
    return;
  }
  els.reviewCollectibleSelect.innerHTML = state.collectibles.map((item) => '<option value="' + item.id + '">' + escapeHtml(formatCollectibleOption(item)) + '</option>').join('');
  if (state.selectedCollectible) els.reviewCollectibleSelect.value = String(state.selectedCollectible.id);
}
function renderReleases() {
  if (!state.selectedCollectible) { els.releaseList.innerHTML = '<div class="empty-state">' + TXT.selectRelease + '</div>'; return; }
  if (!state.releases.length) { els.releaseList.innerHTML = '<div class="empty-state">' + TXT.noRelease + '</div>'; els.releaseStatus.textContent = TXT.noReleaseShort; els.featuredStock.textContent = '--'; return; }
  els.releaseStatus.textContent = state.releases.length + ' ' + TXT.releases;
  els.featuredStock.textContent = cleanText(state.releases.find((item) => item.stock !== undefined)?.stock, TXT.unlimited);
  els.releaseList.innerHTML = state.releases.map((item) => {
    const isFlash = Number(item.type) === 1;
    return '<article class="release-card"><div><h3>' + escapeHtml(cleanText(item.title, TXT.unnamedRelease)) + '</h3><p>' + escapeHtml(cleanText(item.subTitle, TXT.followOfficial)) + '</p><p>' + escapeHtml(cleanText(item.rules, TXT.rules)) + '</p><div class="release-price-row"><span class="release-pay">' + money(item.payValue) + '</span><span>' + TXT.original + ' ' + money(item.actualValue) + '</span></div></div><button class="rush-btn" type="button" data-rush="' + item.id + '" ' + (isFlash ? '' : 'disabled') + '>' + (isFlash ? TXT.rush : TXT.normalSale) + '</button></article>';
  }).join('');
}
function renderReviews() {
  if (!state.reviews.length) { els.reviewFeed.innerHTML = '<div class="empty-state">' + TXT.noReviews + '</div>'; return; }
  els.reviewFeed.innerHTML = state.reviews.map((review) => '<article class="review-card">' + artwork(review.images, review.title, 'review-image') + '<div><div class="review-meta"><strong>' + escapeHtml(cleanText(review.name, TXT.chPlayer)) + '</strong><span>' + escapeHtml(cleanText(review.createTime, '').slice(0, 10)) + '</span></div><h3>' + escapeHtml(cleanText(review.title, TXT.untitledReview)) + '</h3><p>' + escapeHtml(cleanText(review.content, TXT.noReviewContent)) + '</p><div class="review-actions"><button class="' + (review.isLike ? 'liked' : '') + '" type="button" data-like="' + review.id + '">' + TXT.like + ' ' + (review.liked ?? 0) + '</button><button type="button" data-likes="' + review.id + '">' + TXT.likedUsers + '</button><span>' + (review.comments ?? 0) + ' ' + TXT.comments + '</span></div></div></article>').join('');
  wireImageFallbacks(els.reviewFeed);
}
async function loadMe() { if (!state.token) { state.user = null; state.profile = null; renderUser(); renderProfile(); return; } try { state.user = await api('/user/me'); } catch (_error) { state.token = ''; state.user = null; state.profile = null; localStorage.removeItem('collectorhub_token'); } renderUser(); renderProfile(); }
async function loadProfile() { if (!state.token || !state.user) { state.profile = null; renderProfile(); return; } try { state.profile = await api('/user/profile'); if (state.profile) { state.user = { ...(state.user || {}), id: state.profile.id, nickName: state.profile.nickName, icon: state.profile.icon }; renderUser(); } } catch (error) { state.profile = null; showToast(error.message); } renderProfile(); }
async function uploadAvatar(file) {
  if (!file) {
    els.avatarUploadStatus.textContent = TXT.chooseAvatar;
    return '';
  }
  const body = new FormData();
  body.append('file', file);
  els.avatarUploadStatus.textContent = TXT.avatarUploading;
  const uploaded = await api('/upload/icons', { method: 'POST', body });
  els.editIcon.value = uploaded ? '/imgs' + uploaded : '';
  els.editIconPreview.src = imageUrl(els.editIcon.value);
  els.avatarUploadStatus.textContent = TXT.avatarUploaded;
  wireImageFallbacks(document.querySelector('#profile-edit'));
  return els.editIcon.value;
}
async function handleAvatarChange(event) {
  const file = event.target.files[0];
  if (!file) return;
  try {
    await uploadAvatar(file);
  } catch (error) {
    els.avatarUploadStatus.textContent = error.message;
  }
}
async function saveProfile(event) {
  event.preventDefault();
  const genderValue = els.editGender.value;
  const payload = {
    nickName: els.editNickName.value.trim(),
    icon: els.editIcon.value.trim(),
    city: els.editCity.value.trim(),
    introduce: els.editIntroduce.value.trim(),
    gender: genderValue === '' ? null : genderValue === 'true',
    birthday: els.editBirthday.value || null
  };
  try {
    els.profileEditStatus.textContent = TXT.profileSaving;
    state.profile = await api('/user/profile', { method: 'PUT', body: JSON.stringify(payload) });
    if (state.profile) state.user = { ...(state.user || {}), id: state.profile.id, nickName: state.profile.nickName, icon: state.profile.icon };
    renderUser();
    renderProfile();
    closeProfileEdit();
    showToast(TXT.profileSaved);
  } catch (error) {
    els.profileEditStatus.textContent = error.message;
  }
}
async function loadTypes() { try { state.types = await api('/collectible-types/list') || []; } catch (error) { state.types = []; showToast(error.message); } renderTypes(); }
async function loadCollectibles(params = {}) { state.activeType = params.typeId || 'all'; setLoading(els.collectibleGrid, TXT.loadingToys); const path = params.typeId && params.typeId !== 'all' ? '/collectibles/of/type?typeId=' + encodeURIComponent(params.typeId) : '/collectibles/of/name?name=' + encodeURIComponent(params.name || ''); try { state.collectibles = await api(path) || []; state.selectedCollectible = state.collectibles[0] || null; renderTypes(); renderCollectibles(); renderCollectibleOptions(); renderSelected(); await loadReleases(); } catch (error) { state.collectibles = []; renderCollectibles(); renderCollectibleOptions(); showToast(error.message); } }
async function selectCollectible(id) { try { state.selectedCollectible = await api('/collectibles/' + id); } catch (error) { state.selectedCollectible = state.collectibles.find((item) => String(item.id) === String(id)); showToast(error.message); } renderCollectibles(); renderSelected(); await loadReleases(); showView('drops'); }
async function loadReleases() { if (!state.selectedCollectible) { renderReleases(); return; } setLoading(els.releaseList, TXT.loadingDrops); try { state.releases = await api('/release-items/list/' + state.selectedCollectible.id) || []; } catch (error) { state.releases = []; showToast(error.message); } renderReleases(); }
async function loadReviews(myOnly = false) { setLoading(els.reviewFeed, TXT.loadingReviews); try { state.reviews = await api(myOnly ? '/reviews/of/me' : '/reviews/hot') || []; } catch (error) { state.reviews = []; showToast(error.message); } renderReviews(); }
async function handleReviewCollectibleChange() { const item = state.collectibles.find((collectible) => String(collectible.id) === els.reviewCollectibleSelect.value); if (!item) return; state.selectedCollectible = item; renderCollectibles(); renderSelected(); await loadReleases(); }
async function sendCode() { const phone = $('#phoneInput').value.trim(); if (!phone) { els.authStatus.textContent = TXT.phoneRequired; return; } try { await api('/user/code?phone=' + encodeURIComponent(phone), { method: 'POST' }); els.authStatus.textContent = TXT.codeSent; } catch (error) { els.authStatus.textContent = error.message; } }
async function login(event) { event.preventDefault(); const phone = $('#phoneInput').value.trim(); const code = $('#codeInput').value.trim(); try { const token = await api('/user/login', { method: 'POST', body: JSON.stringify({ phone, code }) }); state.token = token; localStorage.setItem('collectorhub_token', token); els.authStatus.textContent = TXT.loginOk; await loadMe(); await loadProfile(); await loadReviews(); showView('profile'); } catch (error) { els.authStatus.textContent = error.message; } }
async function logout() { try { await api('/user/logout', { method: 'POST' }); } catch (_error) {} state.token = ''; state.user = null; state.profile = null; localStorage.removeItem('collectorhub_token'); renderUser(); renderProfile(); showToast(TXT.logoutOk); }
async function rushBuy(id) { try { const orderId = await api('/flash-sale-orders/rush/' + id, { method: 'POST' }); showToast(TXT.orderSubmitted + orderId); } catch (error) { showToast(error.message); } }
async function likeReview(id) { try { await api('/reviews/like/' + id, { method: 'PUT' }); await loadReviews(); } catch (error) { showToast(error.message); } }
async function showReviewLikes(id) { try { const users = await api('/reviews/likes/' + id) || []; const names = users.map((user) => user.nickName || user.name || TXT.player + ' ' + user.id).join('\u3001'); showToast(names ? TXT.recentLikes + names : TXT.noLikes); } catch (error) { showToast(error.message); } }
async function uploadReviewImage(file) { if (!file) return ''; const body = new FormData(); body.append('file', file); return api('/upload/reviews', { method: 'POST', body }); }
async function publishReview(event) { event.preventDefault(); const collectibleId = Number(els.reviewCollectibleSelect.value); const title = $('#reviewTitle').value.trim(); const content = $('#reviewContent').value.trim(); const file = $('#reviewImage').files[0]; if (!collectibleId || !title || !content) { els.reviewStatus.textContent = TXT.reviewRequired; return; } try { els.reviewStatus.textContent = TXT.publishing; const uploaded = await uploadReviewImage(file); const images = uploaded ? '/imgs' + uploaded : ''; const reviewId = await api('/reviews', { method: 'POST', body: JSON.stringify({ collectibleId, title, content, images }) }); els.reviewStatus.textContent = TXT.published + ' ' + reviewId; $('#reviewForm').reset(); if (state.selectedCollectible) els.reviewCollectibleSelect.value = String(state.selectedCollectible.id); await loadReviews(); showView('community'); } catch (error) { els.reviewStatus.textContent = error.message; } }
function bindEvents() {
  document.querySelector('.bottom-tabs').addEventListener('click', (event) => { const button = event.target.closest('[data-target]'); if (button) showView(button.dataset.target); });
  els.userBadge.addEventListener('click', () => showView('profile'));
  $('#sendCodeBtn').addEventListener('click', sendCode);
  $('#loginForm').addEventListener('submit', login);
  $('#logoutBtn').addEventListener('click', logout);
  $('#profileLogoutBtn').addEventListener('click', logout);
  $('#editProfileBtn').addEventListener('click', openProfileEdit);
  $('#cancelEditProfileBtn').addEventListener('click', closeProfileEdit);
  $('#profileEditForm').addEventListener('submit', saveProfile);
  $('#editIconFile').addEventListener('change', handleAvatarChange);
  $('#refreshBtn').addEventListener('click', () => loadCollectibles());
  $('#searchForm').addEventListener('submit', (event) => { event.preventDefault(); loadCollectibles({ name: $('#searchInput').value.trim() }); });
  $('#reviewForm').addEventListener('submit', publishReview);
  els.reviewCollectibleSelect.addEventListener('change', handleReviewCollectibleChange);
  $('#myReviewsBtn').addEventListener('click', () => loadReviews(true));
  els.typeList.addEventListener('click', (event) => { const button = event.target.closest('[data-type]'); if (button) loadCollectibles({ typeId: button.dataset.type }); });
  els.collectibleGrid.addEventListener('click', (event) => { const button = event.target.closest('[data-id]'); if (button) selectCollectible(button.dataset.id); });
  els.releaseList.addEventListener('click', (event) => { const button = event.target.closest('[data-rush]'); if (button && !button.disabled) rushBuy(button.dataset.rush); });
  els.reviewFeed.addEventListener('click', (event) => { const like = event.target.closest('[data-like]'); const likes = event.target.closest('[data-likes]'); if (like) likeReview(like.dataset.like); if (likes) showReviewLikes(likes.dataset.likes); });
}
async function boot() { bindEvents(); renderUser(); renderProfile(); window.addEventListener('hashchange', () => showView(location.hash.replace('#', '') || 'discover', false)); showView(location.hash.replace('#', '') || 'discover', false); await loadMe(); await Promise.all([loadProfile(), loadTypes(), loadReviews()]); await loadCollectibles(); }
boot();
