Cypress.on('uncaught:exception', (err, runnable) => {
  return false;
});

describe('Edit Vehicle Test', () => {
  it('Login, get enterprise, get vehicle, edit brand, check', () => {
    //мой Login
    cy.visit('http://localhost:8080/login')
    cy.get("#username").type("man1");
    cy.get("#password").type("1111");
    cy.wait(1000)
    cy.get("button[type='submit']").click();

    cy.get('#enterprisesTable .enterprise-row', { timeout: 5000 }).should('have.length.at.least', 1);
    cy.get('#enterprisesTable .enterprise-row').should('contain', 'Из Импорта');
    cy.get('#enterprisesTable .enterprise-row')
      .contains('Из Импорта')
      .click();

    cy.get('#paginationInfo', { timeout: 10000 })
      .should('not.contain', '0 из 0');

    cy.get('#addCarBtn').click();
    cy.get('#carModal', { timeout: 5000 }).should('be.visible');
    cy.get('select#brand').then(($select) => {
          cy.wrap($select).select('1');
    });
    cy.wait(1000)
    cy.get("#registrationNumber").type("P314KH314", { force: true });

    cy.get('#saveCarBtn').click();
    cy.wait(5000)
    cy.get('#carModal').should('not.be.visible');

    cy.get('#carsTableBody tr').should('contain', 'P314KH314');
    cy.get('#carsTableBody tr').contains('P314KH314').click();

    cy.get('#carsTableBody tr')
      .contains('P314KH314')
      .closest('tr')
      .as('selectedCar');
    cy.get('@selectedCar')
      .find('button.edit-car-btn')
      .click();

    cy.get('#carModal', { timeout: 5000 }).should('be.visible');
    //cy.get('select#brand').should('contain', 'Lada');
    cy.wait(5000)
    //в окне редактирования (если Lada, меняю на Mersedes и наоборот)
    cy.get('select#brand').then(($select) => {
      const curBrandId = $select.val();
      const newBrandId = curBrandId === '1' ? '2' : '1';

      cy.wrap($select).select(newBrandId);

      cy.get(`select#brand option[value="${newBrandId}"]`)
        .invoke('text')
        .as('brandNameForAssert');
    });

    cy.get('#saveCarBtn').click();
    cy.wait(5000)
    cy.get('#carModal').should('not.be.visible');

    cy.get('#carsTableBody tr')
      .contains('P314KH314')
      .closest('tr')
      .within(() => {
        cy.get('@brandNameForAssert').then((expectedName) => {
          cy.get('td').eq(0).should('have.text', expectedName);
        });
      });

    //теперь удаляем
    cy.get('#carsTableBody tr')
          .contains('P314KH314')
          .closest('tr')
          .as('selectedCar');
    cy.get('@selectedCar')
          .find('button.delete-car-btn')
          .click();

    cy.get('#confirmDeleteModal', { timeout: 5000 }).should('be.visible');
    cy.get('#confirmDeleteBtn').click();
    cy.wait(5000)
    cy.get('#carModal').should('not.be.visible');

    cy.get('#carsTableBody tr').should('not.contain', 'P314KH314');

  });

})