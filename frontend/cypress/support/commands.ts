export {}

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      /**
       * Finds the grocery item row (as rendered by GroceryItemComponent) that contains
       * the given text, e.g. an item's name.
       */
      itemRow(text: string): Chainable<JQuery<HTMLElement>>

      /**
       * Finds whichever grocery item row is currently in edit mode. Unlike itemRow(),
       * this doesn't rely on text content, so it keeps working after the row's fields
       * have been changed to something not yet asserted on.
       */
      editingRow(): Chainable<JQuery<HTMLElement>>
    }
  }
}

Cypress.Commands.add('itemRow', (text: string) => {
  return cy.contains('.border-b.p-2', text)
})

Cypress.Commands.add('editingRow', () => {
  return cy.get('.border-b.p-2:has([aria-label="Speichern"])')
})
