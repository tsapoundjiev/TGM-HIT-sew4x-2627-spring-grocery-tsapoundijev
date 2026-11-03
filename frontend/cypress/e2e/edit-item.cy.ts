import {visitAndLoad} from '../support/testData'

function interceptUpdate() {
  return cy.intercept('PUT', '/api/grocery/item', (req) => {
    req.reply({ statusCode: 200, body: req.body })
  }).as('updateItem')
}

describe('Editing a grocery item', () => {
  beforeEach(() => {
    visitAndLoad()
  })

  it('switches the row into edit mode with the current values prefilled', () => {
    cy.itemRow('Milch').find('[aria-label="Bearbeiten"]').click()

    cy.editingRow().find('input[type="number"]').should('have.value', '2')
    cy.editingRow().find('input[type="text"]').should('have.value', 'Milch')
    cy.editingRow().find('select').should('be.visible')
    cy.editingRow().find('input[type="checkbox"]').should('be.disabled')
  })

  it('saves the edited values and leaves edit mode', () => {
    interceptUpdate()

    cy.itemRow('Milch').find('[aria-label="Bearbeiten"]').click()
    cy.editingRow().find('input[type="number"]').clear().type('5')
    cy.editingRow().find('input[type="text"]').clear().type('Vollmilch')
    cy.editingRow().find('[aria-label="Speichern"]').click()

    cy.wait('@updateItem').its('request.body').should('deep.include', {
      id: 1,
      name: 'Vollmilch',
      amount: 5,
    })

    cy.itemRow('Vollmilch').should('contain.text', '5')
    cy.itemRow('Vollmilch').find('[aria-label="Bearbeiten"]').should('exist')
  })

  it('saves when Enter is pressed in an input', () => {
    interceptUpdate()

    cy.itemRow('Milch').find('[aria-label="Bearbeiten"]').click()
    cy.editingRow().find('input[type="text"]').clear().type('Vollmilch{enter}')

    cy.wait('@updateItem')
    cy.itemRow('Vollmilch').should('exist')
  })

  it('discards changes without contacting the backend when cancelled', () => {
    cy.intercept('PUT', '/api/grocery/item').as('updateItem')

    cy.itemRow('Milch').find('[aria-label="Bearbeiten"]').click()
    cy.editingRow().find('input[type="text"]').clear().type('Vollmilch')
    cy.editingRow().find('[aria-label="Abbrechen"]').click()

    cy.get('@updateItem.all').should('have.length', 0)
    cy.itemRow('Milch').find('[aria-label="Bearbeiten"]').should('exist')
    cy.contains('.border-b.p-2', 'Vollmilch').should('not.exist')
  })

  it('discards changes without contacting the backend when Escape is pressed', () => {
    cy.intercept('PUT', '/api/grocery/item').as('updateItem')

    cy.itemRow('Milch').find('[aria-label="Bearbeiten"]').click()
    cy.editingRow().find('input[type="text"]').clear().type('Vollmilch{esc}')

    cy.get('@updateItem.all').should('have.length', 0)
    cy.itemRow('Milch').should('exist')
    cy.contains('.border-b.p-2', 'Vollmilch').should('not.exist')
  })
})
