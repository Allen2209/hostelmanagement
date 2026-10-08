/**
 * Hostel Management System - Client Side Script
 */

document.addEventListener('DOMContentLoaded', () => {

    // Responsive portal sidebar
    const sidebarToggle = document.querySelector('.sidebar-toggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', () => {
            const isOpen = document.body.classList.toggle('sidebar-visible');
            sidebarToggle.setAttribute('aria-expanded', String(isOpen));
        });

        document.addEventListener('click', (event) => {
            if (!document.body.classList.contains('sidebar-visible')) {
                return;
            }
            const sidebar = document.getElementById('portalSidebar');
            if (sidebar && !sidebar.contains(event.target) && !sidebarToggle.contains(event.target)) {
                document.body.classList.remove('sidebar-visible');
                sidebarToggle.setAttribute('aria-expanded', 'false');
            }
        });
    }

    // 1. Date of Birth Auto-formatting (DD-MM-YYYY)
    const dobInputs = document.querySelectorAll('.dob-mask');
    dobInputs.forEach(input => {
        input.addEventListener('input', (e) => {
            let value = e.target.value.replace(/\D/g, ''); // strip non-digits
            if (value.length > 8) {
                value = value.substring(0, 8);
            }

            let formatted = '';
            if (value.length > 0) {
                formatted = value.substring(0, 2);
            }
            if (value.length >= 3) {
                formatted += '-' + value.substring(2, 4);
            }
            if (value.length >= 5) {
                formatted += '-' + value.substring(4, 8);
            }

            e.target.value = formatted;
        });

        input.addEventListener('blur', (e) => {
            validateDob(e.target);
        });
    });

    // 2. Validate DOB helper
    function validateDob(input) {
        const val = input.value.trim();
        const pattern = /^(\d{2})-(\d{2})-(\d{4})$/;
        if (val && !pattern.test(val)) {
            input.classList.add('is-invalid');
        } else {
            input.classList.remove('is-invalid');
        }
    }

    // 3. Calendar Picker sync helper
    const calendarPickers = document.querySelectorAll('.dob-picker');
    calendarPickers.forEach(picker => {
        const targetInputId = picker.dataset.targetInput;
        const targetInput = document.getElementById(targetInputId);

        if (targetInput && targetInput.value) {
            // If text input has DD-MM-YYYY, set calendar value to YYYY-MM-DD
            const parts = targetInput.value.split('-');
            if (parts.length === 3 && parts[2].length === 4) {
                picker.value = `${parts[2]}-${parts[1]}-${parts[0]}`;
            }
        }

        picker.addEventListener('change', (e) => {
            const dateVal = e.target.value; // YYYY-MM-DD
            if (dateVal && targetInput) {
                const [year, month, day] = dateVal.split('-');
                targetInput.value = `${day}-${month}-${year}`;
                targetInput.classList.remove('is-invalid');
            }
        });
    });

    // 4. Copy Account Number button
    const copyBtn = document.getElementById('copyAccountBtn');
    if (copyBtn) {
        copyBtn.addEventListener('click', () => {
            const accNumEl = document.getElementById('generatedAccountText');
            if (accNumEl) {
                const text = accNumEl.innerText.trim();
                navigator.clipboard.writeText(text).then(() => {
                    const originalText = copyBtn.innerHTML;
                    copyBtn.innerHTML = '✓ Copied!';
                    setTimeout(() => {
                        copyBtn.innerHTML = originalText;
                    }, 2000);
                });
            }
        });
    }

    // 5. Basic client-side form validation feedback
    const forms = document.querySelectorAll('form.needs-validation');
    forms.forEach(form => {
        form.addEventListener('submit', (e) => {
            let valid = true;
            const requiredFields = form.querySelectorAll('[required]');

            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    field.classList.add('is-invalid');
                    valid = false;
                } else {
                    field.classList.remove('is-invalid');
                }
            });

            const dob = form.querySelector('.dob-mask');
            if (dob && dob.value) {
                const pattern = /^(\d{2})-(\d{2})-(\d{4})$/;
                if (!pattern.test(dob.value.trim())) {
                    dob.classList.add('is-invalid');
                    valid = false;
                }
            }

            if (!valid) {
                e.preventDefault();
            }
        });
    });
});
