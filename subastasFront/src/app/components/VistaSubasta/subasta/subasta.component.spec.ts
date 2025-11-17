import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubastaComponent } from './subasta.component';

describe('SubastaComponent', () => {
  let component: SubastaComponent;
  let fixture: ComponentFixture<SubastaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubastaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SubastaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
