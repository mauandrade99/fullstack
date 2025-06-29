<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dashboard</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/favicon.png">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard"><i class="fas fa-home"></i> Home</a>
        <button id="logout-button" class="btn btn-outline-light">Sair <i class="fas fa-sign-out-alt"></i></button>
    </div>
</nav>

<main class="container mt-4">
    <div id="welcome-message"></div>
    <div class="row g-4">
        <div class="col-12 col-lg-7" id="admin-section">
            <div class="card h-100">
                <div class="card-header">
                    <h3><i class="fas fa-users"></i> Usuários</h3>
                </div>
                <div class="card-body">
                    <div id="users-loader" class="text-center">
                        <div class="spinner-border" role="status"><span class="visually-hidden">Carregando...</span></div>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-striped table-hover d-none" id="users-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Nome</th>
                                    <th>Email</th>
                                    <th>Role</th>
                                    <th style="min-width: 110px;">Ações</th> 
                                </tr>
                            </thead>
                            <tbody id="users-table-body">
                                <!-- As linhas da tabela serão inseridas aqui pelo JavaScript -->
                            </tbody>
                        </table>
                    </div>

                    <nav aria-label="Paginação de usuários" class="mt-4 d-none" id="pagination-controls">
                        <ul class="pagination justify-content-center">
                            <li class="page-item" id="prev-page-item">
                                <a class="page-link" href="#" id="prev-page-btn">Anterior</a>
                            </li>
                            <li class="page-item disabled">
                                <span class="page-link" id="page-info">1&nbsp;de&nbsp;1</span>
                            </li>
                            <li class="page-item" id="next-page-item">
                                <a class="page-link" href="#" id="next-page-btn">Próxima</a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </div>
        </div>
        <div class="col-12 col-lg-5" id="address-column">
            <div class="card h-100">
                <div class="card-header d-flex justify-content-between align-items-center">
                        <h3 class="mb-0"><i class="fas fa-map-marker-alt"></i> Endereços</h3>
                        <button class="btn btn-primary d-none" data-bs-toggle="modal" style="max-width: 40px;" data-bs-target="#address-modal" id="add-address-btn">
                            <i class="fas fa-plus"></i> 
                        </button>
                </div>
                <div class="card-body" id="addresses-section">
                    <div id="addresses-user-info"></div>
                    <div id="addresses-placeholder"><p class="text-muted">Selecione um usuário na lista (função de admin) ou veja seus próprios endereços.</p></div>
                    <div id="addresses-loader" class="text-center d-none">
                        <div class="spinner-border text-secondary" role="status"><span class="visually-hidden">Carregando...</span></div>
                    </div>
                    <div id="addresses-content" class="mt-3"></div>
                </div>
            </div>
        </div>
    </div>
</main>

<!-- Modal de Confirmação de Exclusão -->
<div class="modal fade" id="confirm-delete-modal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header text-black">
                <h5 class="modal-title">Confirmar Exclusão</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p>Tem certeza que deseja excluir este endereço? Esta ação não pode ser desfeita.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <button type="button" class="btn btn-danger" id="confirm-delete-btn">Sim, Excluir</button>
            </div>
        </div>
    </div>
</div>

<!-- Modal para Editar Usuário -->
<div class="modal fade" id="user-edit-modal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="user-modal-title">Editar Usuário</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="user-edit-form">
                    <input type="hidden" id="user-edit-id">
                    <div class="mb-3">
                        <label for="user-edit-name" class="form-label">Nome</label>
                        <input type="text" class="form-control" id="user-edit-name" required>
                    </div>
                    <div class="mb-3">
                        <label for="user-edit-email" class="form-label">Email (não pode ser alterado)</label>
                        <input type="email" class="form-control" id="user-edit-email" readonly>
                    </div>
                    <div class="mb-3">
                        <label for="user-edit-role" class="form-label">Role (Permissão)</label>
                        <select class="form-select" id="user-edit-role" required>
                            <option value="ROLE_USER">Usuário Comum (USER)</option>
                            <option value="ROLE_ADMIN">Administrador (ADMIN)</option>
                        </select>
                    </div>
                    <div id="user-modal-error" class="alert alert-danger d-none"></div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <button type="button" class="btn btn-primary" id="save-user-btn">Salvar Alterações</button>
            </div>
        </div>
    </div>
</div>

<!-- Modal para Adicionar/Editar Endereço -->
<div class="modal fade" id="address-modal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modal-title">Adicionar Novo Endereço</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="address-form">
                    <input type="hidden" id="address-id">
                    <div class="mb-3">
                        <label for="cep" class="form-label">CEP (apenas números)</label>
                        <input type="text" class="form-control" id="cep" required maxlength="8">
                    </div>
                    <div class="mb-3">
                        <label for="logradouro" class="form-label">Logradouro</label>
                        <input type="text" class="form-control" id="logradouro" readonly="true">
                    </div>
                    <div class="form-group row">
                        <div class="mb-3 col-md-9">
                            <label for="cidade" class="form-label">Cidade</label>
                            <input type="text" class="form-control" id="cidade"  readonly="true">
                        </div>
                        <div class="mb-3 col-md-3">
                            <label for="uf" class="form-label">UF</label>
                            <input type="text" class="form-control" id="uf"  readonly="true" >
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="numero" class="form-label">Número</label>
                        <input type="text" class="form-control" id="numero" required>
                    </div>
                    <div class="mb-3">
                        <label for="complemento" class="form-label">Complemento</label>
                        <input type="text" class="form-control" id="complemento">
                    </div>
                    <div id="modal-error" class="alert alert-danger d-none"></div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <button type="button" class="btn btn-primary" id="save-address-btn">Salvar</button>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script> const contextPath = '${pageContext.request.contextPath}'; console.log(contextPath);</script>
<script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
    
</body>
</html>