package ru.practicum.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.comment.dto.CommentCreatedDto;
import ru.practicum.comment.dto.FullCommentDto;
import ru.practicum.comment.model.Comment;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring", uses = Comment.class)
public interface CommentMapper {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Mapping(target = "authorId", expression = "java(comment.getAuthor().getId())")
    @Mapping(target = "eventId", expression = "java(comment.getEvent().getId())")
    @Mapping(target = "created", expression = "java(comment.getCreated().format(formatter))")
    CommentCreatedDto toCommentCreatedDto(Comment comment);

    @Mapping(target = "authorId", expression = "java(comment.getAuthor().getId())")
    @Mapping(target = "eventId", expression = "java(comment.getEvent().getId())")
    @Mapping(target = "created", expression = "java(comment.getCreated().format(formatter))")
    @Mapping(target = "updated", expression = "java(comment.getUpdated().format(formatter))")
    FullCommentDto toFullCommentDto(Comment comment);

    List<FullCommentDto> toFullCommentDtoList(List<Comment> comments);
}
