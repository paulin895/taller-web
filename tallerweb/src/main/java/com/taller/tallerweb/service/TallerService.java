package com.taller.tallerweb.service;

import com.taller.tallerweb.model.Taller;
import com.taller.tallerweb.repository.TallerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TallerService {

    @Autowired
    private TallerRepository tallerRepository;

    public Taller guardar(Taller taller) {
        return tallerRepository.save(taller);
    }

    public List<Taller> listarTodos() {
        return tallerRepository.findAll();
    }

    public Optional<Taller> buscarPorId(Long id) {
        return tallerRepository.findById(id);
    }

    public void eliminar(Long id) {
        tallerRepository.deleteById(id);
    }
}
