
package es.upm.grise.profundizacion.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrderTest {

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
    }

    /**
     * Test: Añadir un item válido (producto nuevo, precio >= 0, cantidad > 0)
     */
    @Test
    void testAddValidItem() throws IncorrectItemException {
        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(10.0);
        when(item.getQuantity()).thenReturn(2);
        when(item.getProduct()).thenReturn(mock(Product.class));

        order.addItem(item);

        Collection<Item> items = order.getItems();
        assertEquals(1, items.size());
        assertTrue(items.stream().anyMatch(i -> i.getQuantity() == 2));
    }

    /**
     * Test: Añadir item con cantidad = 0 → debe lanzar IncorrectItemException
     */
    @Test
    void testAddItemQuantityZeroThrowsException() {
        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(10.0);
        when(item.getQuantity()).thenReturn(0);
        when(item.getProduct()).thenReturn(mock(Product.class));

        assertThrows(IncorrectItemException.class, () -> order.addItem(item));
    }

    /**
     * Test: Añadir item con cantidad negativa → debe lanzar IncorrectItemException
     */
    @Test
    void testAddItemQuantityNegativeThrowsException() {
        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(10.0);
        when(item.getQuantity()).thenReturn(-5);
        when(item.getProduct()).thenReturn(mock(Product.class));

        assertThrows(IncorrectItemException.class, () -> order.addItem(item));
    }

    /**
     * Test: Añadir item con precio negativo → debe lanzar IncorrectItemException
     */
    @Test
    void testAddItemPriceNegativeThrowsException() {
        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(-1.0);
        when(item.getQuantity()).thenReturn(1);
        when(item.getProduct()).thenReturn(mock(Product.class));

        assertThrows(IncorrectItemException.class, () -> order.addItem(item));
    }

    /**
     * Test: Añadir dos items con productos distintos → ambos deben añadirse
     */
    @Test
    void testAddTwoDifferentProducts() throws IncorrectItemException {
        Product p1 = mock(Product.class);
        Product p2 = mock(Product.class);

        Item item1 = mock(Item.class);
        when(item1.getPrice()).thenReturn(10.0);
        when(item1.getQuantity()).thenReturn(1);
        when(item1.getProduct()).thenReturn(p1);

        Item item2 = mock(Item.class);
        when(item2.getPrice()).thenReturn(20.0);
        when(item2.getQuantity()).thenReturn(1);
        when(item2.getProduct()).thenReturn(p2);

        order.addItem(item1);
        order.addItem(item2);

        assertEquals(2, order.getItems().size());
    }

    /**
     * Test: Añadir item con mismo producto y mismo precio → incrementa cantidad.
     * Verificamos que setQuantity se llama con 5 sobre el item ya existente (item1).
     */
    @Test
    void testAddSameProductSamePriceIncrementsQuantity() throws IncorrectItemException {
        Product p = mock(Product.class);

        Item item1 = mock(Item.class);
        when(item1.getPrice()).thenReturn(10.0);
        when(item1.getQuantity()).thenReturn(2);
        when(item1.getProduct()).thenReturn(p);

        Item item2 = mock(Item.class);
        when(item2.getPrice()).thenReturn(10.0);
        when(item2.getQuantity()).thenReturn(3);
        when(item2.getProduct()).thenReturn(p);

        order.addItem(item1); // first added: becomes the existing
        order.addItem(item2); // merge into item1

        // Size remains 1 (merged)
        assertEquals(1, order.getItems().size());

        // Verify that the existing item (item1) had its quantity set to 2 + 3 = 5
        verify(item1, times(1)).setQuantity(5);

        // Optional: ensure we didn't set on the second item
        verify(item2, never()).setQuantity(anyInt());
    }

    /**
     * Test: Añadir item con mismo producto pero precio distinto → se añade nuevo item
     */
    @Test
    void testAddSameProductDifferentPriceAddsNewItem() throws IncorrectItemException {
        Product p = mock(Product.class);

        Item item1 = mock(Item.class);
        when(item1.getPrice()).thenReturn(10.0);
        when(item1.getQuantity()).thenReturn(2);
        when(item1.getProduct()).thenReturn(p);

        Item item2 = mock(Item.class);
        when(item2.getPrice()).thenReturn(15.0);
        when(item2.getQuantity()).thenReturn(1);
        when(item2.getProduct()).thenReturn(p);

        order.addItem(item1);
        order.addItem(item2);

        assertEquals(2, order.getItems().size());

        // No merge → no setQuantity should be called on either
        verify(item1, never()).setQuantity(anyInt());
        verify(item2, never()).setQuantity(anyInt());
    }

    /**
     * Test: Variación de orden → primero distinto precio, luego mismo precio.
     * Verificamos que el merge se hace sobre el item existente con precio 10 (item2).
     */
    @Test
    void testOrderVariationAddsCorrectly() throws IncorrectItemException {
        Product p = mock(Product.class);

        Item item1 = mock(Item.class);
        when(item1.getPrice()).thenReturn(15.0);
        when(item1.getQuantity()).thenReturn(1);
        when(item1.getProduct()).thenReturn(p);

        Item item2 = mock(Item.class);
        when(item2.getPrice()).thenReturn(10.0);
        when(item2.getQuantity()).thenReturn(2);
        when(item2.getProduct()).thenReturn(p);

        Item item3 = mock(Item.class);
        when(item3.getPrice()).thenReturn(10.0);
        when(item3.getQuantity()).thenReturn(3);
        when(item3.getProduct()).thenReturn(p);

        order.addItem(item1); // 15.0
        order.addItem(item2); // 10.0
        order.addItem(item3); // 10.0 → should merge into item2

        // After merge, we should still have only two items (15 and 10)
        assertEquals(2, order.getItems().size());

        // Verify that the existing 10.0 item (item2) had its quantity set to 2 + 3 = 5
        verify(item2, times(1)).setQuantity(5);

        // Optional: ensure we didn't set on others
        verify(item1, never()).setQuantity(anyInt());
        verify(item3, never()).setQuantity(anyInt());
    }
}
