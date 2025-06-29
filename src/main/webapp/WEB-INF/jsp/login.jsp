<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/favicon.png">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    </head>
<body>

<div class="full-height-container">
    <div class="col-10 col-sm-10 col-md-10 col-lg-6 col-xl-4">
        <div class="card shadow-sm border-0 login-card-custom">
            <div class="card-body p-4">
                <h3 class="card-title text-center mb-4">Login</h3>
                <form id="login-form" action="${pageContext.request.contextPath}/api/auth/authenticate" method="post">
                    <div class="mb-3">
                        <label for="email" class="form-label">Email</label>
                        <input type="email" class="form-control" id="email" required>
                    </div>
                        <div class="mb-3">
                        <label for="password" class="form-label">Senha</label>
                        <div class="input-group">
                            <input type="password" class="form-control" id="password" required>
                            <button class="btn btn-outline-secondary" type="button" id="togglePassword">
                                <i class="fas fa-eye" id="togglePasswordIcon"></i>
                            </button>
                        </div>
                    </div>
                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary">
                            <span id="login-spinner" class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
                            Entrar
                        </button>
                    </div>
                    <div id="error-message" class="alert alert-danger mt-3 d-none" role="alert"></div>
                </form>
                <div class="text-center mt-3">
                    <p>Não tem uma conta? <a href="${pageContext.request.contextPath}/register">Registre-se aqui</a></p>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script> const contextPath = '${pageContext.request.contextPath}'; console.log(contextPath);</script>
<script src="${pageContext.request.contextPath}/js/login.js"></script>
</body>
</html>