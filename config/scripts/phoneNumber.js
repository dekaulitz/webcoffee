const prefix = "+62";
load("config/scripts/node_modules/crypto-js/crypto-js.js");
var $phoneNumber = function () {
  return prefix + "" + Math.ceil(Math.random() * 9999999999);
}

var $email = function () {
  return randomString(10) + "@" + randomString(5) + ".com";
}
var $secret = function () {
  return CryptoJS.SHA256("Message").toString();
}

function randomString(length) {
  let result = '';
  let characters = 'abcdefghijklmnopqrstuvwxyz0123456789';
  let charactersLength = characters.length;
  for (let i = 0; i < length; i++) {
    result += characters.charAt(Math.floor(Math.random() * charactersLength));
  }
  return result;
}
