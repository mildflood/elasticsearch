import { MAXDSWEBPage } from './app.po';

describe('MAXDS-WEB App', () => {
  let page: MAXDSWEBPage;

  beforeEach(() => {
    page = new MAXDSWEBPage();
  });

  it('should display welcome message', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('Welcome to app!!');
  });
});
