package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findAllByOwnerOrderById(User owner);

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true " +
            "AND (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%')))")
    List<Item> searchAvailableItems(String text);

    boolean existsByOwnerId(Integer ownerId);

    /*List<Item> findAllByRequestId(Integer requestId);

    List<Item> findAllByOwnerId(Integer ownerId);

    @Query("SELECT i FROM Item i " +
            "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND i.available = true")
    List<Item> searchAvailableItems(String text, Pageable pageable);

    List<Item> findAllByOwnerOrderById(User owner, Pageable pageable);

    @Query("SELECT i FROM Item i " +
            "WHERE i.owner.id = ?1 " +
            "ORDER BY i.id")
    List<Item> findAllByOwnerIdOrderById(Integer ownerId);
    */
}