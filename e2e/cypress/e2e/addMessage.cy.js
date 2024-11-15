context('add message', () => {
    let msgText = 'msg' + new Date().getTime();

    it('create message', () => {
        cy.loginAdmin();
        cy.createMessage(msgText);
    })

});
