import {testLists, visitAndLoad} from '../support/testData'

describe('Loading and displaying grocery data', () => {
  beforeEach(() => {
    visitAndLoad()
  })

  it('populates the list selector with every fetched list', () => {
    cy.get('nav select').find('option').should('have.length', testLists.length + 2)
    cy.get('nav select').contains('option', 'Alle Listen')
    cy.get('nav select').contains('option', 'Ohne Liste')
    testLists.forEach((list) => {
      cy.get('nav select').contains('option', list.name)
    })
  })

  it('displays every item from every list', () => {
    cy.itemRow('Milch')
    cy.itemRow('Butter')
    cy.itemRow('Apfel')
    cy.itemRow('Schrauben')
    cy.get('.border-b.p-2').should('have.length', 4)
  })

  it('shows the list name next to each item', () => {
    cy.itemRow('Milch').should('contain.text', 'Supermarkt')
    cy.itemRow('Schrauben').should('contain.text', 'Baumarkt')
  })

  it('renders collected items with a strikethrough style', () => {
    cy.itemRow('Butter').find('.flex.flex-1.flex-wrap.gap-2').should('have.class', 'line-through')
    cy.itemRow('Milch').find('.flex.flex-1.flex-wrap.gap-2').should('not.have.class', 'line-through')
  })

  it('shows the correct count of collected vs. total items', () => {
    cy.contains('1 von 4 Artikel eingesammelt')
  })

  it('sorts uncollected items alphabetically before collected items', () => {
    cy.get('.border-b.p-2').should(($rows) => {
      const texts = $rows.toArray().map((el) => el.textContent || '')
      expect(texts[0], 'row 0').to.contain('Apfel')
      expect(texts[1], 'row 1').to.contain('Milch')
      expect(texts[2], 'row 2').to.contain('Schrauben')
      expect(texts[3], 'row 3').to.contain('Butter')
    })
  })
})
