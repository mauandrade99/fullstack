    const registerForm = document.getElementById('register-form');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirm-password');
    const togglePasswordBtn = document.getElementById('togglePassword');
    const togglePasswordIcon = document.getElementById('togglePasswordIcon');
    const errorMessageDiv = document.getElementById('error-message');
    const successMessageDiv = document.getElementById('success-message');
    const spinner = document.getElementById('register-spinner');

    togglePasswordBtn.addEventListener('click', function () {
        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordInput.setAttribute('type', type);
        confirmPasswordInput.setAttribute('type', type); 

        togglePasswordIcon.classList.toggle('fa-eye');
        togglePasswordIcon.classList.toggle('fa-eye-slash');
    });

-
    registerForm.addEventListener('submit', function(event) {
        event.preventDefault();

        const name = document.getElementById('name').value;
        const email = document.getElementById('email').value;
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;
        
        errorMessageDiv.classList.add('d-none');
        successMessageDiv.classList.add('d-none');

        // --- Validação de Confirmação de Senha (no lado do cliente) ---
        if (password !== confirmPassword) {
            errorMessageDiv.textContent = 'As senhas não coincidem. Por favor, tente novamente.';
            errorMessageDiv.classList.remove('d-none');
            return; 
        }

        spinner.classList.remove('d-none');
        document.getElementById('register-btn').disabled = true;

        // Consumo da API REST para registro
        fetch(contextPath + '/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nome: name, email: email, senha: password })
        })
        .then(function(response) {
            if (!response.ok) {
                return response.json().then(function(err) { 
                    throw new Error(err.details ? err.details.join(', ') : 'Erro ao registrar.');
                });
            }
            return response.json();
        })
        .then(function(data) {
            // Sucesso!
            successMessageDiv.textContent = 'Registro realizado com sucesso! Redirecionando para o login...';
            successMessageDiv.classList.remove('d-none');
            setTimeout(function() {
                window.location.href = contextPath+'/login'; 
            }, 2000);
        })
        .catch(function(error) {
            errorMessageDiv.textContent = error.message;
            errorMessageDiv.classList.remove('d-none');
        })
        .finally(function() {
            spinner.classList.add('d-none');
            document.getElementById('register-btn').disabled = false;
        });
    });