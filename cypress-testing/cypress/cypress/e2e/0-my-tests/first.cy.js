describe('My First Test', () => {
  it('should see hello', () => {
    cy.visit('http://localhost:8080')
    cy.contains('Hello')
  })
})