package com.example.Usuario.Service;

import com.example.Usuario.Model.Usuario;

public interface UsuarioService {

    /**
     * Registra un nuevo usuario en la base de datos.
     * @param usuario Objeto Usuario con todos los datos
     * @return Usuario guardado en la base de datos
     */
    Usuario register(Usuario usuario);
}
