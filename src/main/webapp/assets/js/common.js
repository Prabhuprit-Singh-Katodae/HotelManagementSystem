/* Small helpers used on every page */
document.addEventListener('DOMContentLoaded', function () {

  // 1) Add the CSRF token (from <meta name="csrf">) to every POST form
  var meta = document.querySelector('meta[name="csrf"]');
  if (meta && meta.content) {
    document.querySelectorAll('form').forEach(function (form) {
      if ((form.getAttribute('method') || '').toLowerCase() === 'post' && !form.querySelector('input[name="_csrf"]')) {
        var input = document.createElement('input');
        input.type = 'hidden';
        input.name = '_csrf';
        input.value = meta.content;
        form.appendChild(input);
      }
    });
  }

  // 2) Ask before dangerous actions:  <form data-confirm="Delete this?">  or  <button data-confirm="...">
  document.querySelectorAll('form[data-confirm]').forEach(function (form) {
    form.addEventListener('submit', function (e) {
      if (!window.confirm(form.getAttribute('data-confirm'))) e.preventDefault();
    });
  });

  // 3) Date inputs: check-out must be after check-in
  var checkIn = document.querySelector('input[name="checkIn"]');
  var checkOut = document.querySelector('input[name="checkOut"]');
  if (checkIn && checkOut) {
    var sync = function () {
      if (!checkIn.value) return;
      var d = new Date(checkIn.value);
      d.setDate(d.getDate() + 1);
      var min = d.toISOString().slice(0, 10);
      checkOut.min = min;
      if (checkOut.value && checkOut.value < min) checkOut.value = min;
    };
    checkIn.addEventListener('change', sync);
    sync();
  }

  // 4) Mobile menu buttons
  var navToggle = document.getElementById('navToggle');
  if (navToggle) navToggle.addEventListener('click', function () {
    document.getElementById('navLinks').classList.toggle('open');
  });
  var menuBtn = document.getElementById('menuBtn');
  if (menuBtn) menuBtn.addEventListener('click', function () {
    document.getElementById('sidebar').classList.toggle('open');
  });

  // 5) Success alerts disappear after a few seconds
  document.querySelectorAll('.alert-success').forEach(function (a) {
    setTimeout(function () { a.style.display = 'none'; }, 6000);
  });
});
