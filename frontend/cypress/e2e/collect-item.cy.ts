import {findItem, visitAndLoad} from '../support/testData'

function interceptPatch() {
  return cy.intercept('PATCH', /\/api\/grocery\/item\/\d+(\?.*)?$/, (req) => {
    const [, idString] = req.url.match(/\/item\/(\d+)/) ?? []
    const collected = req.url.includes('collected=true')
    req.reply({ statusCode: 200, body: { ...findItem(Number(idString)), collected } })
  }).as('patchCollected')
}

describe('Marking a grocery item as collected', () => {
  beforeEach(() => {
    visitAndLoad()
  })

  it('sends a request marking the item as collected and updates the styling', () => {
    interceptPatch()

    cy.itemRow('Milch').find('input[type="checkbox"]').as('checkbox')
    cy.get('@checkbox').should('not.be.checked')
    cy.get('@checkbox').click()

    cy.wait('@patchCollected').its('request.url').should('include', 'collected=true')
    cy.get('@checkbox').should('be.checked')
    cy.itemRow('Milch').find('.flex.flex-1.flex-wrap.gap-2').should('have.class', 'line-through')
    cy.contains('2 von 4 Artikel eingesammelt')
  })

  it('sends a request marking the item as not collected and updates the styling', () => {
    interceptPatch()

    cy.itemRow('Butter').find('input[type="checkbox"]').as('checkbox')
    cy.get('@checkbox').should('be.checked')
    cy.get('@checkbox').click()

    cy.wait('@patchCollected').its('request.url').should('include', 'collected=false')
    cy.get('@checkbox').should('not.be.checked')
    cy.itemRow('Butter').find('.flex.flex-1.flex-wrap.gap-2').should('not.have.class', 'line-through')
    cy.contains('0 von 4 Artikel eingesammelt')
  })

  it('moves a newly collected item to the end of the sorted list', () => {
    interceptPatch()

    cy.itemRow('Milch').find('input[type="checkbox"]').click()
    cy.wait('@patchCollected')

    // Uncollected items (Apfel, Schrauben) stay first, sorted alphabetically;
    // collected items (Butter, Milch) move to the end, also sorted alphabetically.
    cy.get('.border-b.p-2').should(($rows) => {
      const texts = $rows.toArray().map((el) => el.textContent || '')
      expect(texts[0], 'row 0').to.contain('Apfel')
      expect(texts[1], 'row 1').to.contain('Schrauben')
      expect(texts[2], 'row 2').to.contain('Butter')
      expect(texts[3], 'row 3').to.contain('Milch')
    })
  })
})
