import {visitAndLoad} from '../support/testData'

describe('Adding a new grocery item', () => {
  beforeEach(() => {
    visitAndLoad()
    cy.get('.sticky.bottom-0').as('newItemRow')
  })

  it('shows an empty, invalid form by default', () => {
    cy.get('@newItemRow').find('input[type="text"]').should('have.value', '')
    cy.get('@newItemRow').find('[aria-label="Erstellen"]').should('be.disabled')
  })

  it('enables submission once a name is entered', () => {
    cy.get('@newItemRow').find('input[type="text"]').type('Kaffee')
    cy.get('@newItemRow').find('[aria-label="Erstellen"]').should('be.enabled')
  })

  it('rejects a name longer than 255 characters', () => {
    const longName = 'a'.repeat(256)
    cy.get('@newItemRow').find('input[type="text"]').type(longName, { delay: 0 }).blur()

    cy.get('@newItemRow').find('input[type="text"]').should('have.class', 'border-red-600')
    cy.get('@newItemRow').find('[aria-label="Erstellen"]').should('be.disabled')
  })

  it('sends the entered data to the backend and adds the item to the list', () => {
    cy.intercept('POST', '/api/grocery/item', (req) => {
      req.reply({ statusCode: 201, body: { ...req.body, id: 999 } })
    }).as('createItem')

    cy.get('@newItemRow').find('input[type="text"]').type('Kaffee')
    cy.get('@newItemRow').find('input[type="number"]').clear().type('3')
    cy.get('@newItemRow').find('select').select('Supermarkt')
    cy.get('@newItemRow').find('[aria-label="Erstellen"]').click()

    cy.wait('@createItem').its('request.body').should('deep.include', {
      name: 'Kaffee',
      amount: 3,
      listId: 1,
      collected: false,
    })

    cy.itemRow('Kaffee').should('contain.text', '3').and('contain.text', 'Supermarkt')
    cy.get('@newItemRow').find('input[type="text"]').should('have.value', '')
    cy.get('@newItemRow').find('input[type="number"]').should('have.value', '1')
  })

  it('clears the form when cancelled and does not contact the backend', () => {
    cy.intercept('POST', '/api/grocery/item').as('createItem')

    cy.get('@newItemRow').find('input[type="text"]').type('Kaffee')
    cy.get('@newItemRow').find('[aria-label="Abbrechen"]').click()

    cy.get('@createItem.all').should('have.length', 0)
    cy.get('@newItemRow').find('input[type="text"]').should('have.value', '')
    cy.contains('.border-b.p-2', 'Kaffee').should('not.exist')
  })

  it('defaults the list selector to the currently filtered list', () => {
    cy.get('nav select').select('Baumarkt')
    cy.get('@newItemRow').find('select').should('have.value', '2')
  })

  it('switches the list selector when a different filter is chosen', () => {
    cy.get('nav select').select('Supermarkt')
    cy.get('@newItemRow').find('select').should('have.value', '1')

    cy.get('nav select').select('Baumarkt')
    cy.get('@newItemRow').find('select').should('have.value', '2')
  })

  it('resets the list selector to "Ohne Liste" when the filter is cleared', () => {
    cy.get('nav select').select('Baumarkt')
    cy.get('@newItemRow').find('select').should('have.value', '2')

    cy.get('nav select').select('Alle Listen')
    cy.get('@newItemRow').find('select option:selected').should('have.text', 'Ohne Liste')
  })

  it('sends the filtered list id when creating without touching the selector', () => {
    cy.intercept('POST', '/api/grocery/item', (req) => {
      req.reply({ statusCode: 201, body: { ...req.body, id: 999 } })
    }).as('createItem')

    cy.get('nav select').select('Baumarkt')
    cy.get('@newItemRow').find('input[type="text"]').type('Dübel')
    cy.get('@newItemRow').find('[aria-label="Erstellen"]').click()

    cy.wait('@createItem').its('request.body').should('deep.include', {
      name: 'Dübel',
      listId: 2,
    })
  })
})
