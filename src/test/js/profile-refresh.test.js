const fs = require('fs');
const path = require('path');
const assert = require('assert');

const appPath = path.resolve(__dirname, '../../../frontend/nginx-1.18.0/html/collectorhub/app.js');
const indexPath = path.resolve(__dirname, '../../../frontend/nginx-1.18.0/html/collectorhub/index.html');
const source = fs.readFileSync(appPath, 'utf8');
const html = fs.readFileSync(indexPath, 'utf8');

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
