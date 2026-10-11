package com.reservas.apidereservas.service.security;

import com.reservas.apidereservas.entity.User;

public interface CurrentUserProvider {

    User getCurrentUser();

}
