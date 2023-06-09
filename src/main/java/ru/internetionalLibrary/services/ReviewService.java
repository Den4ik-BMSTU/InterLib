package ru.internetionalLibrary.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.internetionalLibrary.exceptions.EmptyResultFromDataBaseException;
import ru.internetionalLibrary.models.Review;
import ru.internetionalLibrary.storage.interf.ReviewStorage;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Qualifier("daoReviewStorage")
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage<Review> reviewStorage;

    public List<Review> getAll() {
        return reviewStorage.getAll();
    }

    public List<Review> findReviewsByParams(Map<String, Integer> params) {
        return reviewStorage.getByParams(params);
    }

    public Review create(Review review) {
        if (!reviewStorage.checkBookExists(review.getFilmId())) {
            throw new EmptyResultFromDataBaseException("Фильм с идентификатором " + review.getFilmId() + " отсутствует");
        }

        if (!reviewStorage.checkUserExists(review.getUserId())) {
            throw new EmptyResultFromDataBaseException("Пользователь с идентификатором " + review.getUserId() + " отсутствует");
        }

        Review createdReview = reviewStorage.create(review);

        return createdReview;
    }

    public Review update(Review review) {
        if (!reviewStorage.checkBookExists(review.getFilmId())) {
            throw new EmptyResultFromDataBaseException("Фильм с идентификатором " + review.getFilmId() + " отсутствует");
        }
        if (!reviewStorage.checkUserExists(review.getUserId())) {
            throw new EmptyResultFromDataBaseException("Пользователь с идентификатором " + review.getUserId() + " отсутствует");
        }

        Review updatedReview = reviewStorage.update(review);
        
        return updatedReview;
    }

    public Review getByReviewId(Long reviewId) {
        return reviewStorage.getById(reviewId);
    }

    public void addLikeToReview(Integer reviewId, Integer userId) {
        if (!reviewStorage.checkUserExists(userId)) {
            throw new EmptyResultFromDataBaseException("Пользователь с идентификатором " + userId + " отсутствует");
        }

        if (!reviewStorage.checkReviewExists(reviewId)) {
            throw new EmptyResultFromDataBaseException("Отзыв с идентификатором " + reviewId + " отсутствует");
        }
        reviewStorage.addLikeToReview(reviewId, userId);
    }

    public void addDisLikeToReview(Integer reviewId, Integer userId) {
        if (!reviewStorage.checkUserExists(userId)) {
            throw new EmptyResultFromDataBaseException("Пользователь с идентификатором " + userId + " отсутствует");
        }

        if (!reviewStorage.checkReviewExists(reviewId)) {
            throw new EmptyResultFromDataBaseException("Отзыв с идентификатором " + reviewId + " отсутствует");
        }
        reviewStorage.addDisLikeToReview(reviewId, userId);
    }

    public void delete(Long reviewId) {
        if (!reviewStorage.checkReviewExists(Math.toIntExact(reviewId))) {
            throw new EmptyResultFromDataBaseException("Отзыв с идентификатором " + reviewId + " отсутствует");
        }
        reviewStorage.delete(reviewId);
    }
}
