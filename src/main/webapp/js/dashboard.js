 
    // Variáveis globais para o script
    const token = localStorage.getItem('jwt_token');
    let currentUserIdForAddress = null; 
    const addressModal = new bootstrap.Modal(document.getElementById('address-modal'));
    const userEditModal = new bootstrap.Modal(document.getElementById('user-edit-modal'));
    const confirmDeleteModal = new bootstrap.Modal(document.getElementById('confirm-delete-modal'));
    

    // Função para decodificar o token JWT
    function parseJwt(token) {
        try {
            return JSON.parse(atob(token.split('.')[1]));
        } catch (e) {
            return null;
        }
    }

    // Função para buscar e exibir os endereços de um usuário
    function fetchAndDisplayAddresses(userId, token) {
         
         const addressesPlaceholder = document.getElementById('addresses-placeholder');
         const addressesLoader = document.getElementById('addresses-loader');
         const addressesContent = document.getElementById('addresses-content');
         const addAddressBtn = document.getElementById('add-address-btn');
         
         if ( userId == 0 ) {
            
            addressesPlaceholder.classList.remove('d-none');
            addressesLoader.classList.add('d-none');
            addressesContent.innerHTML = '';
            addAddressBtn.classList.add('d-none');
            document.getElementById('addresses-user-info').innerHTML = '';

         } else {

            currentUserIdForAddress = userId;
            addressesPlaceholder.classList.add('d-none');
            addressesLoader.classList.remove('d-none');
            addressesContent.innerHTML = '';
            addAddressBtn.classList.remove('d-none');

            fetch(contextPath + '/api/users/' + userId + '/addresses', 
            { 
                headers: { 'Authorization': 'Bearer ' + token } 
            })
            .then(function(res) { return res.json(); })
            .then(function(addresses) {
                addressesLoader.classList.add('d-none');
                if (addresses.length === 0) {
                    addressesContent.innerHTML = '<p class="text-muted">Nenhum endereço cadastrado.</p>';
                } else {
                    const addressList = document.createElement('ul');
                    addressList.className = 'list-group';
                    addresses.forEach(function(addr) {
                        const item = document.createElement('li');
                        item.className = 'list-group-item d-flex justify-content-between align-items-center';
                        
                        item.innerHTML = 
                            '<div>' +
                                '<strong>' + addr.logradouro + ', ' + addr.numero + '</strong>' + (addr.complemento == ''?'':', '+addr.complemento) + '<br>' +
                                addr.bairro + ' - ' + addr.cidade + '/' + addr.estado + '<br>' +
                                'CEP: ' + addr.cep +
                            '</div>' +
                            '<div>' +
                                '<button class="btn btn-sm btn-primary me-2" onclick=\'editAddress(' + JSON.stringify(addr) + ')\'><i class="fas fa-edit"></i></button>' +
                                '<button class="btn btn-sm btn-danger" onclick="deleteAddress(' + addr.id + ', ' + userId + ')"><i class="fas fa-trash"></i></button>' +
                            '</div>';
                        addressList.appendChild(item);
                    });
                    addressesContent.appendChild(addressList);
                }
            }).catch(function(err) {
                addressesLoader.classList.add('d-none');
                addressesContent.innerHTML = '<div class="alert alert-danger">Erro ao carregar endereços.</div>';
            });
        }
    }


    let currentPage = 0;
    const pageSize = 8; 

    function fetchUsersForAdmin(token, page = 0) {
        currentPage = page; 

        const usersList = document.getElementById('users-list');
        const loader = document.getElementById('users-loader');
        const paginationControls = document.getElementById('pagination-controls');

        loader.style.display = 'block';
        usersList.innerHTML = '';
        paginationControls.classList.add('d-none'); 

        const url = contextPath + '/api/users?page=' + page + '&size=' + pageSize + '&sort=nome,asc';

        fetch(url, { headers: { 'Authorization': 'Bearer ' + token } })
        .then(function(res) { return res.json(); })
        .then(function(pageData) {
            loader.style.display = 'none';
            
            pageData.content.forEach(function(user) {
                const listItem = document.createElement('div'); 
                listItem.className = 'list-group-item d-flex justify-content-between align-items-center';

                const userData = parseJwt(token);
                const loggedInUserId = userData.userId;


                let deleteButtonHtml = '';
               
                if (user.id !== loggedInUserId) {
                    deleteButtonHtml = '<button class="btn btn-sm btn-danger" onclick="confirmUserDelete(' + user.id + ')"><i class="fas fa-trash"></i></button>';
                } else {
                    deleteButtonHtml = '<button class="btn btn-sm btn-disabled disabled" disabled="true" onclick="confirmUserDelete(' + user.id + ')"><i class="fas fa-trash"></i></button>';
                }

                let editButtonHtml = '<button class="btn btn-sm btn-primary me-2" onclick=\'editUser(' + JSON.stringify(user) + ')\'><i class="fas fa-edit"></i></button>';

                listItem.innerHTML = 
                    '<a href="#" class="text-decoration-none text-dark flex-grow-1 user-select-link">' + user.nome + ' (' + user.email + ')' + '</a>' +
                     editButtonHtml + deleteButtonHtml;

                listItem.querySelector('.user-select-link').addEventListener('click', function(e) {
                    e.preventDefault();
                    document.querySelectorAll('#users-list .list-group-item').forEach(function(item) {
                        item.classList.remove('active');
                    });
                    listItem.classList.add('active');
                    document.getElementById('addresses-user-info').innerHTML = '<strong>Exibindo endereços de: ' + user.nome + '</strong>';
                    fetchAndDisplayAddresses(user.id, token);
                });

                fetchAndDisplayAddresses(0);
                
                usersList.appendChild(listItem);
            });

            updatePaginationControls(pageData);
        })
        .catch(function(err) {
            console.error("Erro no fetch de usuários:", err);
            loader.innerHTML = '<div class="alert alert-danger">Erro ao carregar usuários.</div>';
        });
    }

    function updatePaginationControls(pageData) {
        const paginationControls = document.getElementById('pagination-controls');
        const prevPageItem = document.getElementById('prev-page-item');
        const nextPageItem = document.getElementById('next-page-item');
        const pageInfo = document.getElementById('page-info');

        paginationControls.classList.remove('d-none');

        pageInfo.textContent = 'Página ' + (pageData.number + 1) + ' de ' + pageData.totalPages;

        if (pageData.first) {
            prevPageItem.classList.add('disabled');
        } else {
            prevPageItem.classList.remove('disabled');
        }

        if (pageData.last) {
            nextPageItem.classList.add('disabled');
        } else {
            nextPageItem.classList.remove('disabled');
        }
    }

    function editAddress(address) {
        document.getElementById('modal-title').textContent = 'Editar Endereço';
        document.getElementById('address-form').reset();
        document.getElementById('address-id').value = address.id;
        document.getElementById('cep').value = address.cep;
        document.getElementById('numero').value = address.numero;
        document.getElementById('complemento').value = address.complemento;
        document.getElementById('logradouro').value = address.logradouro;
        document.getElementById('cidade').value = address.cidade;
        document.getElementById('uf').value = address.estado;
        addressModal.show();
    }

    function confirmUserDelete(userId) {
        const confirmBtn = document.getElementById('confirm-delete-btn');
        const modalBody = document.querySelector('#confirm-delete-modal .modal-body');
        
        modalBody.innerHTML = '<p>Tem certeza que deseja excluir este <strong>usuário</strong>? Todos os seus endereços também serão excluídos. Esta ação não pode ser desfeita.</p>';
        confirmDeleteModal.show();
        
        confirmBtn.onclick = function() {
            confirmDeleteModal.hide();
            
            const token = localStorage.getItem('jwt_token');
            fetch(contextPath + '/api/users/' + userId, {
                method: 'DELETE',
                headers: { 'Authorization': 'Bearer ' + token }
            })
            .then(function(res) {
                if (!res.ok) {
                    
                    return res.text().then(text => { throw new Error('Erro ao excluir usuário: ' + text) });
                }
                
                fetchUsersForAdmin(token, currentPage);

                document.getElementById('addresses-content').innerHTML = '';
                document.getElementById('addresses-user-info').innerHTML = '';
                document.getElementById('add-address-btn').classList.add('d-none');
            })
            .catch(function(err) { 
                alert(err.message); 
            });
        }
    }

    function editUser(user) {
        document.getElementById('user-edit-form').reset();
        document.getElementById('user-modal-error').classList.add('d-none');
        
        document.getElementById('user-edit-id').value = user.id;
        document.getElementById('user-edit-name').value = user.nome;
        document.getElementById('user-edit-email').value = user.email;
        document.getElementById('user-edit-role').value = user.role;
        
        userEditModal.show();
    }


    function deleteAddress(addressId, userId) {
        const confirmBtn = document.getElementById('confirm-delete-btn');

        confirmDeleteModal.show();

        confirmBtn.onclick = function() {

            confirmDeleteModal.hide();
            
            const token = localStorage.getItem('jwt_token');
            fetch(contextPath + '/api/users/' + userId + '/addresses/' + addressId, {
                method: 'DELETE',
                headers: { 'Authorization': 'Bearer ' + token }
            })
            .then(function(res) {
                if (!res.ok) throw new Error('Erro ao excluir endereço.');

                fetchAndDisplayAddresses(userId, token);
            })
            .catch(function(err) { 
                alert(err.message); 
            });
        }
    }

    const saveBtn = document.getElementById('save-address-btn');
    if (saveBtn) {
        saveBtn.addEventListener('click', function() {

            const addressId = document.getElementById('address-id').value;
            const cep = document.getElementById('cep').value;
            const numero = document.getElementById('numero').value;
            const complemento = document.getElementById('complemento').value;
            const modalError = document.getElementById('modal-error');
            
            modalError.classList.add('d-none');
            
            let url = contextPath + '/api/users/' + currentUserIdForAddress + '/addresses';
            let method = 'POST';
            let body = { cep: cep, numero: numero, complemento: complemento };

            if (addressId) {
                url += '/' + addressId;
                method = 'PUT';
                body = { cep: cep, numero: numero, complemento: complemento };
            }

            fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token },
                body: JSON.stringify(body)
            })
            .then(function(res) {
                if (!res.ok) {
                    return res.json().then(function(err) { 
                        throw new Error(err.details ? err.details.join(', ') : 'Erro ao salvar. Verifique os dados.'); 
                    });
                }
                const addressModal = bootstrap.Modal.getInstance(document.getElementById('address-modal'));
                addressModal.hide();
                fetchAndDisplayAddresses(currentUserIdForAddress, token);
            })
            .catch(function(err) {
                console.error("Erro ao salvar endereço:", err);
                modalError.textContent = err.message;
                modalError.classList.remove('d-none');
            });
        });
    } else {
        console.error("Botão 'save-address-btn' não encontrado!");
    }

    const changeCep = document.getElementById('cep');
    if (changeCep) {
        changeCep.addEventListener('change', function() {

            const cep = document.getElementById('cep').value;
            const modalError = document.getElementById('modal-error');
            
            modalError.classList.add('d-none');
            
            let url = 'https://viacep.com.br/ws/'+cep+'/json/';
            let method = 'GET';

            fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' }
            })
            .then(function(res) {
                if (!res.ok) {
                    return res.json().then(function(err) { 
                        throw new Error(err.details ? err.details.join(', ') : 'Erro ao consultar viacep.'); 
                    });
                }
                return res.json();
            })
            .then(function(data) {
                if (data.logradouro != undefined) {
                    document.getElementById('logradouro').value = data.logradouro;
                    document.getElementById('cidade').value = data.localidade;
                    document.getElementById('uf').value = data.uf;
                    return true;
                } else {
                    throw new Error('Cep Inexistente.'); 
                }
                
            })
            .catch(function(err) {
                console.error("Erro ao consultar viacep:", err);
                modalError.textContent = err.message;
                modalError.classList.remove('d-none');
                return false;
            });
        });
    } else {
        console.error("Cep não encontrado!");
    }


    document.addEventListener('DOMContentLoaded', function() {
        if (!token) {
            window.location.href = contextPath + '/login';
            return;
        }

        const userDataFromToken = parseJwt(token);
        if (!userDataFromToken || !userDataFromToken.userId) {

            localStorage.removeItem('jwt_token');
            window.location.href = contextPath + '/login';
            return;
        }

        const loggedInUserId = userDataFromToken.userId;
        const isAdmin = userDataFromToken.authorities && userDataFromToken.authorities.includes('ROLE_ADMIN');


        fetch(contextPath + '/api/users/' + loggedInUserId, {
            headers: { 'Authorization': 'Bearer ' + token }
        })
        .then(function(response) {
            if (!response.ok) {
                throw new Error('Sessão inválida.');
            }
            return response.json();
        })
        .then(function(userProfile) {

            document.getElementById('welcome-message').innerHTML = '<h2>Bem-vindo, ' + userProfile.nome + '!</h2>';
            

            if (!isAdmin) {
                
                document.getElementById('admin-section').style.display = 'none';
                document.getElementById('address-column').className = 'col-md-12';
                document.getElementById('addresses-placeholder').textContent = '';
                document.getElementById('addresses-user-info').innerHTML = '<strong>Seus Endereços:</strong>';
                fetchAndDisplayAddresses(loggedInUserId, token);
            }
        })
        .catch(function(error) {
            console.error('Erro ao buscar perfil do usuário:', error);
            localStorage.removeItem('jwt_token');
            window.location.href = contextPath + '/login';
        });

        document.getElementById('logout-button').addEventListener('click', function() {
            localStorage.removeItem('jwt_token');
            window.location.href = contextPath + '/login';
        });

        document.getElementById('prev-page-btn').addEventListener('click', function(e) {
            e.preventDefault();
            if (currentPage > 0) {
                fetchUsersForAdmin(token, currentPage - 1);
            }
        });

        document.getElementById('next-page-btn').addEventListener('click', function(e) {
            e.preventDefault();

            fetchUsersForAdmin(token, currentPage + 1);
        });

        document.getElementById('add-address-btn').addEventListener('click', function() {

            document.getElementById('modal-title').textContent = 'Adicionar Novo Endereço';
            document.getElementById('address-form').reset();
            document.getElementById('address-id').value = ''; 
            document.getElementById('cep').readOnly = false;
            document.getElementById('modal-error').classList.add('d-none');
        });

        document.getElementById('save-user-btn').addEventListener('click', function() {
            const userId = document.getElementById('user-edit-id').value;
            const name = document.getElementById('user-edit-name').value;
            const role = document.getElementById('user-edit-role').value;
            const modalError = document.getElementById('user-modal-error');

            modalError.classList.add('d-none');

            const body = { nome: name, role: role };

            fetch(contextPath + '/api/users/' + userId, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token },
                body: JSON.stringify(body)
            })
            .then(function(res) {
                if (!res.ok) {
                    return res.json().then(function(err) { 
                        throw new Error(err.details ? err.details.join(', ') : 'Erro ao atualizar usuário.'); 
                    });
                }
                return res.json();
            })
            .then(function() {
                userEditModal.hide();
                fetchUsersForAdmin(token, currentPage); 
            })
            .catch(function(err) {
                modalError.textContent = err.message;
                modalError.classList.remove('d-none');
            });
        });
        
        if (isAdmin) {
            fetchUsersForAdmin(token, 0);
        }

    });