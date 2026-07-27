// Landing Page Interactivity Script

(function() {
    'use strict';

    // ============================================
    // NAVBAR SCROLL EFFECT
    // ============================================
    const navbar = document.getElementById('navbar');
    const navToggle = document.getElementById('navToggle');
    const navLinks = document.getElementById('navLinks');

    window.addEventListener('scroll', () => {
        navbar.classList.toggle('scrolled', window.scrollY > 20);
    });

    // Mobile nav toggle
    navToggle.addEventListener('click', () => {
        navLinks.classList.toggle('active');
        navToggle.classList.toggle('active');
        document.body.style.overflow = navLinks.classList.contains('active') ? 'hidden' : '';
    });

    // Close mobile nav on link click
    navLinks.querySelectorAll('a').forEach(link => {
        link.addEventListener('click', () => {
            navLinks.classList.remove('active');
            navToggle.classList.remove('active');
            document.body.style.overflow = '';
        });
    });

    // ============================================
    // SCROLL ANIMATIONS (IntersectionObserver)
    // ============================================
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -40px 0px'
    };

    const scrollObserver = new IntersectionObserver((entries) => {
        entries.forEach((entry, index) => {
            if (entry.isIntersecting) {
                setTimeout(() => {
                    entry.target.classList.add('visible');
                }, index * 80);
                scrollObserver.unobserve(entry.target);
            }
        });
    }, observerOptions);

    document.querySelectorAll('.animate-on-scroll').forEach(el => {
        scrollObserver.observe(el);
    });

    // ============================================
    // COUNTER ANIMATION (Hero Stats)
    // ============================================
    const counterObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const counters = entry.target.querySelectorAll('.stat-number[data-target]');
                counters.forEach(counter => {
                    animateCounter(counter);
                });
                counterObserver.unobserve(entry.target);
            }
        });
    }, { threshold: 0.5 });

    const heroStats = document.querySelector('.hero-stats');
    if (heroStats) counterObserver.observe(heroStats);

    function animateCounter(element) {
        const target = parseInt(element.dataset.target, 10);
        const suffix = element.nextElementSibling?.textContent || '';
        const duration = 2000;
        const startTime = performance.now();

        function updateCounter(currentTime) {
            const elapsed = currentTime - startTime;
            const progress = Math.min(elapsed / duration, 1);
            const eased = 1 - Math.pow(1 - progress, 3); // easeOutCubic
            const current = Math.floor(eased * target);
            element.textContent = current.toLocaleString();
            if (progress < 1) {
                requestAnimationFrame(updateCounter);
            } else {
                element.textContent = target.toLocaleString() + suffix;
            }
        }
        requestAnimationFrame(updateCounter);
    }

    // ============================================
    // SMOOTH SCROLL FOR ANCHOR LINKS
    // ============================================
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            const targetId = this.getAttribute('href');
            if (targetId === '#') return;
            const target = document.querySelector(targetId);
            if (target) {
                e.preventDefault();
                const offset = 80; // navbar height
                const targetPosition = target.getBoundingClientRect().top + window.scrollY - offset;
                window.scrollTo({ top: targetPosition, behavior: 'smooth' });
            }
        });
    });

    // ============================================
    // CONTACT FORM HANDLING
    // ============================================
    const contactForm = document.getElementById('contactForm');
    const formSuccess = document.getElementById('formSuccess');
    const submitBtn = contactForm?.querySelector('button[type="submit"]');

    if (contactForm) {
        contactForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const formData = new FormData(contactForm);
            const data = Object.fromEntries(formData.entries());

            // Basic validation
            if (!data.name || !data.email || !data.subject || !data.message) {
                showFormError('Please fill in all fields');
                return;
            }

            if (!isValidEmail(data.email)) {
                showFormError('Please enter a valid email address');
                return;
            }

            // Show loading state
            const originalText = submitBtn.textContent;
            submitBtn.textContent = 'Sending...';
            submitBtn.disabled = true;

            // Simulate API call (replace with actual endpoint)
            try {
                await simulateFormSubmit(data);
                contactForm.reset();
                formSuccess.classList.add('show');
                setTimeout(() => formSuccess.classList.remove('show'), 5000);
            } catch (error) {
                showFormError(error.message || 'Failed to send message. Please try again.');
            } finally {
                submitBtn.textContent = originalText;
                submitBtn.disabled = false;
            }
        });
    }

    function isValidEmail(email) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    }

    function showFormError(message) {
        // Simple toast/alert - you can replace with a nicer notification
        alert(message);
    }

    function simulateFormSubmit(data) {
        return new Promise((resolve) => {
            setTimeout(() => {
                console.log('Form submitted:', data);
                resolve({ success: true });
            }, 1500);
        });
    }

    // ============================================
    // FLOATING SHAPES ANIMATION (Hero Background)
    // ============================================
    const shapes = document.querySelectorAll('.hero-shape');
    let mouseX = 0, mouseY = 0;
    let currentX = 0, currentY = 0;

    document.addEventListener('mousemove', (e) => {
        mouseX = (e.clientX / window.innerWidth - 0.5) * 2;
        mouseY = (e.clientY / window.innerHeight - 0.5) * 2;
    });

    function animateShapes() {
        currentX += (mouseX - currentX) * 0.05;
        currentY += (mouseY - currentY) * 0.05;

        shapes.forEach((shape, i) => {
            const speed = (i + 1) * 0.3;
            const x = currentX * speed * 30;
            const y = currentY * speed * 30;
            shape.style.transform = `translate(${x}px, ${y}px)`;
        });

        requestAnimationFrame(animateShapes);
    }
    animateShapes();

    // ============================================
    // PRICING CARD HOVER EFFECT
    // ============================================
    const pricingCards = document.querySelectorAll('.pricing-card');
    pricingCards.forEach(card => {
        card.addEventListener('mouseenter', () => {
            if (!card.classList.contains('featured')) {
                card.style.transform = 'translateY(-8px) scale(1.02)';
            }
        });
        card.addEventListener('mouseleave', () => {
            if (card.classList.contains('featured')) {
                card.style.transform = 'scale(1.03)';
            } else {
                card.style.transform = '';
            }
        });
    });

    // ============================================
    // TESTIMONIAL CARDS - SUBTLE PARALLAX
    // ============================================
    const testimonialCards = document.querySelectorAll('.testimonial-card');
    window.addEventListener('scroll', () => {
        const scrollY = window.scrollY;
        testimonialCards.forEach((card, i) => {
            const rect = card.getBoundingClientRect();
            const centerY = rect.top + rect.height / 2;
            const distance = (centerY - window.innerHeight / 2) * 0.05;
            card.style.transform = `translateY(${distance}px)`;
        });
    }, { passive: true });

    // ============================================
    // BUTTON RIPPLE EFFECT
    // ============================================
    document.querySelectorAll('.btn').forEach(btn => {
        btn.addEventListener('click', function(e) {
            const rect = this.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;

            const ripple = document.createElement('span');
            ripple.className = 'btn-ripple';
            ripple.style.left = x + 'px';
            ripple.style.top = y + 'px';
            this.appendChild(ripple);

            setTimeout(() => ripple.remove(), 600);
        });
    });

    // ============================================
    // LAZY LOAD IMAGES (if any added later)
    // ============================================
    if ('IntersectionObserver' in window) {
        const imageObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    if (img.dataset.src) {
                        img.src = img.dataset.src;
                        img.removeAttribute('data-src');
                        imageObserver.unobserve(img);
                    }
                }
            });
        });
        document.querySelectorAll('img[data-src]').forEach(img => imageObserver.observe(img));
    }

    // ============================================
    // SCROLL PROGRESS INDICATOR (optional)
    // ============================================
    const progressBar = document.createElement('div');
    progressBar.className = 'scroll-progress';
    document.body.appendChild(progressBar);

    window.addEventListener('scroll', () => {
        const scrollTop = window.scrollY;
        const docHeight = document.documentElement.scrollHeight - window.innerHeight;
        const progress = (scrollTop / docHeight) * 100;
        progressBar.style.width = progress + '%';
    }, { passive: true });

    // ============================================
    // DASHBOARD MOCKUP BAR ANIMATION
    // ============================================
    const chartBars = document.querySelectorAll('.chart-bar');
    const chartObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.animationPlayState = 'running';
                chartObserver.unobserve(entry.target);
            }
        });
    }, { threshold: 0.5 });

    chartBars.forEach(bar => chartObserver.observe(bar));

    // ============================================
    // KEYBOARD NAVIGATION SUPPORT
    // ============================================
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && navLinks.classList.contains('active')) {
            navLinks.classList.remove('active');
            navToggle.classList.remove('active');
            document.body.style.overflow = '';
        }
    });

    // ============================================
    // PERFORMANCE: Reduce motion for users who prefer it
    // ============================================
    const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)');
    if (prefersReducedMotion.matches) {
        document.documentElement.style.setProperty('--transition', 'none');
        document.documentElement.style.setProperty('--transition-fast', 'none');
    }

    prefersReducedMotion.addEventListener('change', (e) => {
        document.documentElement.style.setProperty('--transition', e.matches ? 'none' : 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)');
        document.documentElement.style.setProperty('--transition-fast', e.matches ? 'none' : 'all 0.15s ease');
    });

})();