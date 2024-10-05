package com.damnj.booknetworkapi.feedback;

import com.damnj.booknetworkapi.book.Book;
import com.damnj.booknetworkapi.book.BookRepository;
import com.damnj.booknetworkapi.exception.OperationNotPermittedException;
import com.damnj.booknetworkapi.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;

    public Integer save(FeedbackRequest request, Authentication connectedUser) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with id " + request.bookId()));
        if (book.isArchived() || book.isShareable()) {
            throw new OperationNotPermittedException("You can't give feedback for archived or shareable books");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You can't give feedback on your own book");
        }
        Feedback feedback = feedbackMapper.toFeedback(request);
        return null;
    }
}
