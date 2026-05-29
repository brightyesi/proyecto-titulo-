// Notification popup
function closeNotif() {
  const overlay = document.getElementById('notifOverlay');
  if (overlay) {
    overlay.style.animation = 'fadeOut .25s ease forwards';
    setTimeout(() => overlay.remove(), 250);
  }
}

// Cookie bar
function acceptCookies() {
  const bar = document.getElementById('cookieBar');
  if (bar) {
    bar.style.transition = 'opacity .3s, transform .3s';
    bar.style.opacity = '0';
    bar.style.transform = 'translateY(100%)';
    setTimeout(() => bar.remove(), 300);
  }
  localStorage.setItem('cookiesAccepted', 'true');
}

function closeCookies() { acceptCookies(); }

// Product carousel
const track = document.getElementById('productsTrack');
const prevBtn = document.querySelector('.prev-btn');
const nextBtn = document.querySelector('.next-btn');

if (track && prevBtn && nextBtn) {
  const scrollAmount = 240;

  nextBtn.addEventListener('click', () => {
    track.scrollBy({ left: scrollAmount, behavior: 'smooth' });
  });

  prevBtn.addEventListener('click', () => {
    track.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
  });

  // Show/hide arrows based on scroll position
  track.addEventListener('scroll', () => {
    prevBtn.style.opacity = track.scrollLeft > 0 ? '1' : '0.4';
    const atEnd = track.scrollLeft + track.clientWidth >= track.scrollWidth - 4;
    nextBtn.style.opacity = atEnd ? '0.4' : '1';
  });

  prevBtn.style.opacity = '0.4';
}

// Carousel dots (hero)
const dots = document.querySelectorAll('.dot');
let activeIndex = 2;

dots.forEach((dot, i) => {
  dot.addEventListener('click', () => {
    dots[activeIndex].classList.remove('active');
    activeIndex = i;
    dots[activeIndex].classList.add('active');
  });
});

// Auto-rotate hero dots
setInterval(() => {
  if (dots.length === 0) return;
  dots[activeIndex].classList.remove('active');
  activeIndex = (activeIndex + 1) % dots.length;
  dots[activeIndex].classList.add('active');
}, 3500);

// Wishlist button toggle
document.querySelectorAll('.product-wishlist').forEach(btn => {
  btn.addEventListener('click', () => {
    const icon = btn.querySelector('i');
    if (icon.classList.contains('far')) {
      icon.classList.replace('far', 'fas');
      btn.style.color = '#e53935';
    } else {
      icon.classList.replace('fas', 'far');
      btn.style.color = '';
    }
  });
});

// Header sticky shadow
window.addEventListener('scroll', () => {
  const header = document.querySelector('.header');
  if (header) {
    header.style.boxShadow = window.scrollY > 10
      ? '0 2px 12px rgba(0,0,0,.12)'
      : 'none';
  }
});

// Hide cookie bar if already accepted
if (localStorage.getItem('cookiesAccepted') === 'true') {
  const bar = document.getElementById('cookieBar');
  if (bar) bar.remove();
}

// Auto-close notification after 8 seconds if not dismissed
setTimeout(() => {
  const overlay = document.getElementById('notifOverlay');
  if (overlay) closeNotif();
}, 8000);

// Smooth scroll for category links
document.querySelectorAll('a[href="#"]').forEach(link => {
  link.addEventListener('click', e => e.preventDefault());
});

// Cart icon hover effect
const cartIcon = document.querySelector('.cart-icon');
if (cartIcon) {
  cartIcon.addEventListener('mouseenter', () => {
    cartIcon.style.transform = 'scale(1.05)';
  });
  cartIcon.addEventListener('mouseleave', () => {
    cartIcon.style.transform = '';
  });
}

// Add CSS keyframe for fadeOut dynamically
const style = document.createElement('style');
style.textContent = `
  @keyframes fadeOut {
    to { opacity: 0; transform: scale(.95); }
  }
`;
document.head.appendChild(style);
