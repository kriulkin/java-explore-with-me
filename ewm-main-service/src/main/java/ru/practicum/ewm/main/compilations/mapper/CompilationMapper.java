package ru.practicum.ewm.main.compilations.mapper;

import ru.practicum.ewm.main.compilations.dto.CompilationDto;
import ru.practicum.ewm.main.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.main.compilations.model.Compilation;
import ru.practicum.ewm.main.event.mapper.EventMapper;
import ru.practicum.ewm.main.event.model.Event;

import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        return new Compilation(
                newCompilationDto.getTitle(),
                events,
                newCompilationDto.getPinned()
        );
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle(),
                EventMapper.toListEventShortDto(List.copyOf(compilation.getEvents()))
        );
    }

    public static List<CompilationDto> toCompilationDtoList(List<Compilation> compilations) {
        return compilations.stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }
}
