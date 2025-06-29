    const passwordInput = document.getElementById('password');
    const togglePasswordBtn = document.getElementById('togglePassword');
    const togglePasswordIcon = document.getElementById('togglePasswordIcon');

    togglePasswordBtn.addEventListener('click', function () {
        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordInput.setAttribute('type', type);
        togglePasswordIcon.classList.toggle('fa-eye');
        togglePasswordIcon.classList.toggle('fa-eye-slash');
    });


    document.getElementById('login-form').addEventListener('submit', function(event) {
        event.preventDefault(); 

        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const errorMessageDiv = document.getElementById('error-message');
        const spinner = document.getElementById('login-spinner');
       
        spinner.classList.remove('d-none');
        errorMessageDiv.classList.add('d-none');

      
        fetch(contextPath + '/api/auth/authenticate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email: email, senha: password, frontendOrigin: 2 })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Email ou senha inválidos.');
            }
            return response.json();
        })
        .then(data => {
            localStorage.setItem('jwt_token', data.token);
            window.location.href = contextPath + '/dashboard'; 
        })
        .catch(error => {
            errorMessageDiv.textContent = error.message;
            errorMessageDiv.classList.remove('d-none');
        })
        .finally(() => {
            spinner.classList.add('d-none');
        });
    });