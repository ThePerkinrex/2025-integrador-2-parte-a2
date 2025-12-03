package es.upm.grise.profundizacion.order;

import java.util.ArrayList;
import java.util.Collection;

public class Order {

	private Collection<Item> items;

	/*
	 * Constructor
	 * - La lista de items debe estar vacía pero no nula.
	 */
	public Order() {
		// Cumple la especificación: colección inicial vacía y no nula
		this.items = new ArrayList<>();
	}

	/*
	 * Method to code / test
	 *
	 * Reglas:
	 * - price >= 0; si no, IncorrectItemException
	 * - quantity > 0; si no, IncorrectItemException
	 * - Si existe mismo product y mismo price: incrementar quantity del existente
	 * - Si existe mismo product y distinto price: añadir como nuevo item
	 * - En otro caso: añadir item
	 */
	public void addItem(Item item) throws IncorrectItemException {
		// Validación defensiva del parámetro
		if (item == null) {
			throw new IncorrectItemException("Item no puede ser nulo.");
		}

		// Validación de precio: debe ser >= 0
		double price = item.getPrice();
		if (price < 0) {
			// Según especificación, lanzar IncorrectItemException
			throw new IncorrectItemException("El precio del item debe ser mayor o igual a cero.");

		}

		// Validación de cantidad: debe ser > 0
		int quantity = item.getQuantity();
		if (quantity <= 0) {
			// Según especificación, lanzar IncorrectItemException
			throw new IncorrectItemException("La cantidad del item debe ser mayor que cero.");

		}

		// Intentamos encontrar un item existente con el mismo product
		Item sameProductExisting = null;
		for (Item existing : items) {
			// Igualdad de producto (se asume equals bien definido en Product o
			// identificador)
			if (existing.getProduct().equals(item.getProduct()) && Double.compare(existing.getPrice(), price) == 0) {
				sameProductExisting = existing;
				break;
			}
		}

		if (sameProductExisting == null) {
			// No hay ningún item con ese product: añadimos directamente
			items.add(item);
			return;
		}else{
			// Mismo precio: sumamos cantidades en el item existente
			int newQuantity = sameProductExisting.getQuantity() + quantity;
			sameProductExisting.setQuantity(newQuantity);
		}
	}

	/*
	 * Setters/getters
	 */
	public Collection<Item> getItems() {
		return this.items;
	}

}
