import { FilterByEmailPipe } from './filter-by-email.pipe';

describe('FilterByEmailPipe', () => {
  it('create an instance', () => {
    const pipe = new FilterByEmailPipe();
    expect(pipe).toBeTruthy();
  });
});
