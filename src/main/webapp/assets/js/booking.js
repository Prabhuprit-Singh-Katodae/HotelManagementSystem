/* Booking form: live price estimate when the dates are changed */
document.addEventListener('DOMContentLoaded', function () {
  var box = document.getElementById('estimate');
  if (!box) return;
  var rate = parseFloat(box.getAttribute('data-rate'));
  var inEl = document.getElementById('bfCheckIn');
  var outEl = document.getElementById('bfCheckOut');
  var nightsEl = document.getElementById('estNights');
  var totalEl = document.getElementById('estTotal');
  function update() {
    if (!inEl.value || !outEl.value) return;
    var nights = Math.round((new Date(outEl.value) - new Date(inEl.value)) / 86400000);
    if (nights < 1) { nightsEl.textContent = '-'; totalEl.textContent = '-'; return; }
    nightsEl.textContent = nights;
    totalEl.textContent = '\u20B9' + (nights * rate).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  }
  inEl.addEventListener('change', update);
  outEl.addEventListener('change', update);
});
