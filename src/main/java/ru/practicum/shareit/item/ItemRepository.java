package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId);

    List<Item> findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(String name, String description);

    Boolean existsItemByOwnerId(Long ownerId);

    @Query("SELECT i.id FROM Item AS i " +
            "JOIN User As u ON i.owner.id=u.id" +
            " WHERE i.owner.id = ?1")
    List<Long> findAllItemIdByOwnerId(Long ownerId);
}
