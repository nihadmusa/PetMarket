package com.example.animalservice.specification;

import com.example.animalservice.dao.entity.AnimalEntity;
import com.example.animalservice.util.AnimalGender;
import com.example.animalservice.util.AnimalStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class AnimalSpecification {

	public static Specification<AnimalEntity> hasType(String type) {
		return (root, query, cb) ->
				type == null ? cb.conjunction()
						: cb.equal(cb.lower(root.get("type")), type.toLowerCase());
	}

	public static Specification<AnimalEntity> hasBreed(String breed) {
		return (root, query, cb) ->
				breed == null ? cb.conjunction()
						: cb.equal(cb.lower(root.get("breed")), breed.toLowerCase());
	}

	public static Specification<AnimalEntity> hasCity(String city) {
		return (root, query, cb) ->
				city == null ? cb.conjunction()
						: cb.equal(cb.lower(root.get("city")), city.toLowerCase());
	}

	public static Specification<AnimalEntity> hasGender(AnimalGender gender) {
		return (root, query, cb) ->
				gender == null ? cb.conjunction() : cb.equal(root.get("gender"), gender);
	}

	public static Specification<AnimalEntity> hasStatus(AnimalStatus status) {
		return (root, query, cb) ->
				status == null ? cb.conjunction() : cb.equal(root.get("status"), status) ;
	}
	public static Specification<AnimalEntity> notDeleted() {
		return (root, query, cb) ->
				cb.notEqual(root.get("status"), AnimalStatus.DELETED) ;
	}

	public static Specification<AnimalEntity> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
		return (root, query, cb) -> {
			if (minPrice == null && maxPrice == null) {
				return cb.conjunction();
			}
			if (minPrice == null) {
				return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
			}
			if (maxPrice == null) {
				return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
			}
			return cb.between(root.get("price"), minPrice, maxPrice);
		};
	}
}
