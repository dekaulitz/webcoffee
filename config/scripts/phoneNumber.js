const prefix = "+62";
var $phoneNumber = function () {
  return prefix + " " + Math.ceil(Math.random() * 100)
      + "" + Math.ceil(Math.random() * 100)
      + "" + Math.ceil(Math.random() * 100)
      + "" + Math.ceil(Math.random() * 100)
      + "" + Math.ceil(Math.random() * 100)
      + "" + Math.ceil(Math.random() * 100)
      + "" + Math.ceil(Math.random() * 100)
      + "" + Math.ceil(Math.random() * 100)
      + "" + Math.ceil(Math.random() * 100)
      + "" + Math.ceil(Math.random() * 100);
}
