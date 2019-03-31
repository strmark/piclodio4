import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WebRadioComponent } from './web-radio.component';

describe('WebRadioComponent', () => {
  let component: WebRadioComponent;
  let fixture: ComponentFixture<WebRadioComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WebRadioComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WebRadioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
