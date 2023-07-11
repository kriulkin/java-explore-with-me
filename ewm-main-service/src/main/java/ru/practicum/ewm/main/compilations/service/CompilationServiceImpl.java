package ru.practicum.ewm.main.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.compilations.dto.CompilationDto;
import ru.practicum.ewm.main.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.main.compilations.dto.UpdateCompilationRequest;
import ru.practicum.ewm.main.compilations.mapper.CompilationMapper;
import ru.practicum.ewm.main.compilations.model.Compilation;
import ru.practicum.ewm.main.compilations.storage.CompilationStorage;
import ru.practicum.ewm.main.event.storage.EventStorage;
import ru.practicum.ewm.main.exception.NoSuchEntityException;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationStorage compilationStorage;
    private final EventStorage eventStorage;

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getEvents() != null) {
            return CompilationMapper.toCompilationDto(
                    compilationStorage.save(CompilationMapper.toCompilation(
                            newCompilationDto,
                            eventStorage.findByIdIn(newCompilationDto.getEvents()))
                    )
            );
        }
        return CompilationMapper.toCompilationDto(
                compilationStorage.save(CompilationMapper.toCompilation(
                                newCompilationDto,
                                Collections.emptyList()
                        )
                )
        );
    }

    @Override
    @Transactional
    public void deleteCompilation(long compId) {
        try {
            compilationStorage.deleteById(compId);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchEntityException(String.format("No such compilation with id = %s", compId));
        }
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationStorage.findById(compId).orElseThrow(
                () -> new NoSuchEntityException(String.format("No such category with id = %s", compId)
                )
        );

        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(compilation.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null && !compilation.getTitle().isBlank()) {
            compilation.setTitle(compilation.getTitle());
        }
        if (updateCompilationRequest.getEvents() != null) {
            compilation.setEvents(Set.copyOf(eventStorage.findByIdIn(List.copyOf(updateCompilationRequest.getEvents()))));
        }
        return CompilationMapper.toCompilationDto(compilationStorage.save(compilation));
    }

    @Override
    @Transactional
    public CompilationDto getCompilcationById(long compId) {
        return CompilationMapper.toCompilationDto(
                compilationStorage.findById(compId).orElseThrow(
                        () -> new NoSuchEntityException(String.format("No such category with id = %s", compId)
                        )
                )
        );
    }

    @Override
    public List<CompilationDto> getCompilationList(Boolean pinned, int from, int size) {
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        List<Compilation> compilations;

        if (pinned != null) {
            compilations = compilationStorage.findByPinned(pinned, page).getContent();
        } else {
            compilations = compilationStorage.findAll(page).getContent();
        }
        return CompilationMapper.toCompilationDtoList(compilations);
    }
}
