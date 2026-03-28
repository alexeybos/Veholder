// cypress/e2e/sample.cy.js
describe('Spring Boot App Test', () => {
  it('should visit homepage', () => {
    cy.visit('http://localhost:8080')
    cy.contains('Welcome')
  })
})