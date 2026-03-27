package com.fpmislata.secretariavirtual.registro.domain.exception;

public class DocumentoAlreadyRegisteredException extends RuntimeException {

    public DocumentoAlreadyRegisteredException(String documento) {
        super("El documento " + documento + " ya está registrado en este centro.");
    }
}
