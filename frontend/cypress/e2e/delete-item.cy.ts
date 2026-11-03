import {visitAndLoad} from '../support/testData'

describe('Deleting a grocery item', () => {
  beforeEach(() => {
    visitAndLoad()
  })

  it('removes the item from the list once the backend confirms deletion', () => {
    cy.intercept('DELETE', '/api/grocery/item/1', { statusCode: 200 }).as('deleteItem')

    cy.itemRow('Milch').find('[aria-label="Löschen"]').click()

    cy.wait('@deleteItem')
    cy.contains('.border-b.p-2', 'Milch').should('not.exist')
    cy.get('.border-b.p-2').should('have.length', 3)
    cy.contains('1 von 3 Artikel eingesammelt')
  })

  it('does not remove the item if the backend request fails', () => {
    cy.intercept('DELETE', '/api/grocery/item/1', { statusCode: 500, body: 'Fehler' }).as('deleteItem')
    cy.on('window:alert', () => true)

    cy.itemRow('Milch').find('[aria-label="Löschen"]').click()

    cy.wait('@deleteItem')
    cy.itemRow('Milch').should('exist')
  })
})
