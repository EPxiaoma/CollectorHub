const fs = require('fs');
const path = require('path');
const assert = require('assert');

const appPath = path.resolve(__dirname, '../../../frontend/nginx-1.18.0/html/collectorhub/app.js');
const indexPath = path.resolve(__dirname, '../../../frontend/nginx-1.18.0/html/collectorhub/index.html');
const stylesPath = path.resolve(__dirname, '../../../frontend/nginx-1.18.0/html/collectorhub/styles.css');
const source = fs.readFileSync(appPath, 'utf8');
const html = fs.readFileSync(indexPath, 'utf8');
const styles = fs.readFileSync(stylesPath, 'utf8');

const saveProfileMatch = source.match(/async function saveProfile\(event\) \{[\s\S]*?\n\}/);
assert(saveProfileMatch, 'saveProfile function should exist');

const saveProfile = saveProfileMatch[0];
const renderUserIndex = saveProfile.indexOf('renderUser();');
const renderProfileIndex = saveProfile.indexOf('renderProfile();');
const closeProfileEditIndex = saveProfile.indexOf('closeProfileEdit();');

assert(renderUserIndex >= 0, 'saveProfile should refresh the user badge');
assert(renderProfileIndex >= 0, 'saveProfile should refresh the profile panel after saving');
assert(closeProfileEditIndex >= 0, 'saveProfile should close the edit view after saving');
assert(
  renderUserIndex < renderProfileIndex && renderProfileIndex < closeProfileEditIndex,
  'saveProfile should render profile data before returning to the profile view'
);

assert(html.includes('id="profileLogoutBtn"'), 'profile page should include a logout button');
assert(source.includes('profileLogoutBtn: $(\'#profileLogoutBtn\')'), 'profile logout button should be captured in elements');
assert(
  source.includes("$('#profileLogoutBtn').addEventListener('click', logout);"),
  'profile logout button should use the same logout handler'
);
assert(
  html.includes('<button id="userBadge"'),
  'header user badge should be a clickable profile entry'
);
assert(
  source.includes("els.userBadge.addEventListener('click', () => showView('profile'));"),
  'header user badge should navigate to the profile/login page'
);

assert(
  /\[hidden\]\s*\{[^}]*display:\s*none\s*!important/i.test(styles),
  'hidden panels should remain hidden even when component classes set display'
);

assert(
  source.includes("showView('profile');"),
  'login should navigate to the profile page after successful sign in'
);

assert(
  html.includes('id="reviewCollectibleSelect"'),
  'creator form should offer a collectible selector instead of asking for an ID'
);
assert(
  !html.includes('id="reviewCollectibleId"'),
  'creator form should not expose the collectible ID field to users'
);
assert(
  source.includes("reviewCollectibleSelect: $('#reviewCollectibleSelect')"),
  'collectible selector should be captured in elements'
);
assert(
  source.includes('function renderCollectibleOptions()'),
  'collectible options should be rendered from loaded collectible data'
);
assert(
  source.includes("els.reviewCollectibleSelect.addEventListener('change', handleReviewCollectibleChange);"),
  'changing the selector should update the selected collectible'
);
assert(
  source.includes('const collectibleId = Number(els.reviewCollectibleSelect.value);'),
  'publishing should submit the selected collectible value internally'
);
