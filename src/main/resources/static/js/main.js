// RecycleWise — Main JS

// Mobile nav toggle
function toggleNav() {
  const links = document.querySelector('.nav-links');
  if (links) links.classList.toggle('open');
}

// Animate impact bars on scroll
document.addEventListener('DOMContentLoaded', () => {
  const fills = document.querySelectorAll('.impact-fill');

  // Reset widths initially
  fills.forEach(el => {
    const targetWidth = el.style.width;
    el.style.width = '0';
    el.dataset.target = targetWidth;
  });

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        const el = entry.target;
        setTimeout(() => {
          el.style.width = el.dataset.target;
        }, 100);
        observer.unobserve(el);
      }
    });
  }, { threshold: 0.2 });

  fills.forEach(el => observer.observe(el));

  // Animate stat numbers
  const statNums = document.querySelectorAll('.stat-num');
  statNums.forEach(el => {
    const val = parseInt(el.textContent);
    if (isNaN(val)) return;
    let start = 0;
    const step = Math.ceil(val / 20);
    const timer = setInterval(() => {
      start = Math.min(start + step, val);
      el.textContent = start;
      if (start >= val) clearInterval(timer);
    }, 40);
  });

  // Staggered card animations
  const cards = document.querySelectorAll('.item-card, .tip-card, .full-tip-card, .category-card');
  cards.forEach((card, i) => {
    card.style.opacity = '0';
    card.style.transform = 'translateY(20px)';
    card.style.transition = `opacity 0.4s ease ${i * 0.05}s, transform 0.4s ease ${i * 0.05}s`;
  });

  const cardObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.style.opacity = '1';
        entry.target.style.transform = 'translateY(0)';
        cardObserver.unobserve(entry.target);
      }
    });
  }, { threshold: 0.1 });

  cards.forEach(card => cardObserver.observe(card));
});
