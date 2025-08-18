const box = document.getElementById('box');
const margin = document.getElementById('margin');
const border = document.getElementById('border');
const padding = document.getElementById('padding');

const marginValue = document.getElementById('marginValue');
const borderValue = document.getElementById('borderValue');
const paddingValue = document.getElementById('paddingValue');

margin.addEventListener('input', () => {
  box.style.margin = margin.value + "px";
  marginValue.textContent = margin.value + "px";
});

border.addEventListener('input', () => {
  box.style.borderWidth = border.value + "px";
  borderValue.textContent = border.value + "px";
});

padding.addEventListener('input', () => {
  box.style.padding = padding.value + "px";
  paddingValue.textContent = padding.value + "px";
});
