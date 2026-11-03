import {visitAndLoad} from '../support/testData'

describe('Filtering items by list', () => {
  beforeEach(() => {
    visitAndLoad()
  })

  it('shows only the items belonging to the selected list', () => {
    cy.get('nav select').select('Supermarkt')

    cy.itemRow('Milch')
    cy.itemRow('Butter')
    cy.itemRow('Apfel')
    cy.get('.border-b.p-2').should('have.length', 3)
    cy.contains('1 von 3 Artikel eingesammelt')
  })

  it('shows a different subset when another list is selected', () => {
    cy.get('nav select').select('Baumarkt')

    cy.itemRow('Schrauben')
    cy.get('.border-b.p-2').should('have.length', 1)
    cy.contains('0 von 1 Artikel eingesammelt')
  })

  it('shows all items again when the filter is cleared', () => {
    cy.get('nav select').select('Baumarkt')
    cy.get('.border-b.p-2').should('have.length', 1)

    cy.get('nav select').select('Alle Listen')

    cy.get('.border-b.p-2').should('have.length', 4)
    cy.contains('1 von 4 Artikel eingesammelt')
  })
})
