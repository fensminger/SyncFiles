import { EssaiPage } from './app.po';

describe('essai App', function() {
  let page: EssaiPage;

  beforeEach(() => {
    page = new EssaiPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
