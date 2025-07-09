document.addEventListener('DOMContentLoaded', function() {
    // Navbar Toggler
    const hamburger = document.querySelector('.hamburger');
    const navLinks = document.querySelector('.nav-links');

    if (hamburger && navLinks) {
        hamburger.addEventListener('click', function() {
            navLinks.classList.toggle('active');
            hamburger.classList.toggle('open'); // Optional: for hamburger animation
        });

        // Close nav when a link is clicked (for single-page navigation)
        const allNavLinks = document.querySelectorAll('.nav-links a');
        allNavLinks.forEach(link => {
            link.addEventListener('click', function() {
                if (navLinks.classList.contains('active')) {
                    navLinks.classList.remove('active');
                    hamburger.classList.remove('open');
                }
            });
        });
    }

    // Event Category Filter
    const categoryButtons = document.querySelectorAll('.category-btn');
    const eventLists = document.querySelectorAll('.event-list');

    categoryButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Remove active from all buttons
            categoryButtons.forEach(btn => btn.classList.remove('active'));
            // Add active to the clicked button
            this.classList.add('active');

            const targetCategory = this.dataset.category;

            eventLists.forEach(list => {
                if (list.id.startsWith(targetCategory)) {
                    list.classList.add('active-category');
                } else {
                    list.classList.remove('active-category');
                }
            });
        });
    });

    // Back to Top Button functionality
    const backToTopBtn = document.getElementById('backToTopBtn');

    if (backToTopBtn) {
        window.addEventListener('scroll', () => {
            if (window.scrollY > 300) { // Show button after scrolling down 300px
                backToTopBtn.style.display = 'block';
            } else {
                backToTopBtn.style.display = 'none';
            }
        });

        backToTopBtn.addEventListener('click', () => {
            window.scrollTo({
                top: 0,
                behavior: 'smooth' // Smooth scroll to top
            });
        });
    }


    // --- Counter Animation for About Section ---
    const counters = document.querySelectorAll('.count');
    const aboutSection = document.getElementById('about');
    let countersAnimated = false; // Flag to ensure animation runs only once

    const animateCounter = (counter) => {
        const target = parseInt(counter.getAttribute('data-target'));
        const duration = 2000; // Animation duration in milliseconds
        const start = 0;
        let startTime = null;

        const animate = (currentTime) => {
            if (!startTime) startTime = currentTime;
            const progress = (currentTime - startTime) / duration;
            const currentCount = Math.min(Math.floor(progress * target), target);
            counter.textContent = currentCount; // Removed the original '+' check

            // Re-add '+' or 'k' if originally present from data-target
            if (counter.getAttribute('data-target').includes('+')) {
                counter.textContent += '+';
            } else if (counter.getAttribute('data-target').includes('k')) {
                 counter.textContent += 'k';
            }

            if (progress < 1) {
                requestAnimationFrame(animate);
            }
        };

        requestAnimationFrame(animate);
    };

    // Intersection Observer to trigger animation when about section is visible
    const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting && !countersAnimated) {
                counters.forEach(counter => {
                    animateCounter(counter);
                });
                countersAnimated = true; // Set flag to true after animating
                observer.unobserve(aboutSection); // Stop observing after animation
            }
        });
    }, { threshold: 0.5 }); // Trigger when 50% of the section is visible

    if (aboutSection) {
        observer.observe(aboutSection);
    }


    // --- Event Modal Dynamic Population ---
    const eventModal = document.getElementById('eventModal');

    if (eventModal) {
        eventModal.addEventListener('show.bs.modal', function (event) {
            // Button that triggered the modal
            const button = event.relatedTarget;

            // Extract info from data-* attributes
            const eventId = button.getAttribute('data-event-id');
            const eventName = button.getAttribute('data-event-name');
            const eventFees = button.getAttribute('data-event-fees');
            const eventQrPath = button.getAttribute('data-event-qr-path');

            // Update the modal's content
            const modalTitleSpan = eventModal.querySelector('#modalEventNameTitle');
            const modalEventIdInput = eventModal.querySelector('#modalEventIdInput');
            const modalEventNameInput = eventModal.querySelector('#modalEventNameInput');
            const modalEventFeesP = eventModal.querySelector('#modalEventFees');
            const modalEventQR = eventModal.querySelector('#modalEventQR');

            if (modalTitleSpan) modalTitleSpan.textContent = eventName;
            if (modalEventIdInput) modalEventIdInput.value = eventId;
            if (modalEventNameInput) modalEventNameInput.value = eventName;
            if (modalEventFeesP) modalEventFeesP.textContent = 'Rs. ' + eventFees; // Format as currency
            if (modalEventQR) modalEventQR.src = eventQrPath;
        });
    }
});