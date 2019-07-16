package helpers

object B4 {
  import views.html.helper.FieldConstructor
  implicit val myInputText = FieldConstructor(views.html.customFields.inputText.f)
}
